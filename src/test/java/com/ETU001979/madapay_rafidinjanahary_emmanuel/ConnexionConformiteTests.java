package com.ETU001979.madapay_rafidinjanahary_emmanuel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("tp3")
class ConnexionConformiteTests extends ConformiteHttp {

    @Autowired
    private JwtEncoder encodeur;

    @ParameterizedTest
    @ValueSource(strings = {"rasoa", "agent", "admin"})
    void emetUnJetonSigneAvecLeRole(String cle) throws Exception {
        var compte = compteDuJeuDeDonnees(cle);
        var identifiants = JSON.createObjectNode()
                .set("telephone", compte.required("telephone"))
                .set("code_pin", compte.required("code_pin"));
        long avant = Instant.now().getEpochSecond();
        var reponse = corps(connexion(JSON.writeValueAsString(identifiants)), 200);

        assertThat(reponse.required("token_type").stringValue()).isEqualTo("Bearer");
        assertThat(reponse.required("expires_in").longValue()).isEqualTo(3600);
        assertThat(reponse.required("role")).isEqualTo(compte.required("role"));

        var segments = reponse.required("access_token").stringValue().split("\\.", -1);
        assertThat(segments).hasSize(3);
        var decodeur = Base64.getUrlDecoder();
        var entete = JSON.readTree(decodeur.decode(segments[0]));
        var claims = JSON.readTree(decodeur.decode(segments[1]));
        assertThat(entete.required("alg").stringValue()).isEqualTo("HS256");
        assertThat(claims.required("sub").stringValue()).isEqualTo(compteId(cle));
        assertThat(claims.required("role")).isEqualTo(compte.required("role"));
        long emission = claims.required("iat").longValue();
        assertThat(emission).isBetween(avant, Instant.now().getEpochSecond());
        assertThat(claims.required("exp").longValue()).isEqualTo(emission + 3600);
        assertThat(claims.has("code_pin")).isFalse();
        assertThat(claims.has("codePin")).isFalse();

        var mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET_JWT_TEST.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        var contenu = (segments[0] + "." + segments[1]).getBytes(StandardCharsets.US_ASCII);
        assertThat(decodeur.decode(segments[2])).isEqualTo(mac.doFinal(contenu));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"telephone\":\"0321111111\",\"code_pin\":\"0000\"}",
            "{\"telephone\":\"0329999999\",\"code_pin\":\"1234\"}"
    })
    void refuseLesIdentifiantsIncorrects(String identifiants) throws Exception {
        var reponse = connexion(identifiants);
        verifierProbleme(reponse, 401, "NON_AUTHENTIFIE");
        assertThat(reponse.headers().firstValue("WWW-Authenticate")).contains("Bearer");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{}",
            "{\"telephone\":\"0321111111\",\"code_pin\":\"abcd\"}",
            "{\"telephone\":\"0321111111\",\"code_pin\":1111}",
            "{\"telephone\":\"0321111111\",\"code_pin\":null}",
            "{\"telephone\":\"0321111111\",\"code_pin\":\"1111\",\"inconnu\":true}"
    })
    void refuseLesCorpsInvalides(String identifiants) throws Exception {
        verifierProbleme(connexion(identifiants), 400, "VALIDATION");
    }

    @Test
    void refuseLeJsonMalForme() throws Exception {
        verifierProbleme(connexion("{"), 400, "VALIDATION");
    }

    @Test
    void exigeUnJetonBearerSurLesRoutesProtegees() throws Exception {
        var anonyme = envoyerSansAuthentification(requete("/comptes").GET());
        verifierProbleme(anonyme, 401, "NON_AUTHENTIFIE");
        assertThat(anonyme.headers().firstValue("WWW-Authenticate")).contains("Bearer");

        for (var autorisation : List.of("Basic abcdef", "Bearer jeton.invalide.ici")) {
            var demande = requete("/comptes").header("Authorization", autorisation).GET();
            verifierProbleme(envoyerSansAuthentification(demande), 401, "NON_AUTHENTIFIE");
        }
    }

    @Test
    void refuseUnJetonExpireCorrectementSigne() throws Exception {
        var maintenant = Instant.now();
        var claims = JwtClaimsSet.builder()
                .subject(compteId("admin"))
                .claim("role", "ADMIN")
                .issuedAt(maintenant.minusSeconds(3601))
                .expiresAt(maintenant.minusSeconds(1))
                .build();
        var parametres = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        var jetonExpire = encodeur.encode(parametres).getTokenValue();

        var demande = requete("/comptes").header("Authorization", "Bearer " + jetonExpire).GET();
        verifierProbleme(envoyerSansAuthentification(demande), 401, "NON_AUTHENTIFIE");
    }

    @Test
    void seulLeTitulaireOuUnAdminPeutAnnulerLeTransfert() throws Exception {
        var rasoa = compteId("rasoa");
        var beneficiaire = corps(envoyer(requete("/comptes/" + rasoa + "/beneficiaires")
                .header("Content-Type", "application/json").POST(json(Map.of(
                        "nom", "Bénéficiaire annulation", "telephone", "0388999001")))), 201);
        var transfert = corps(envoyer(requete("/transferts").header("Content-Type", "application/json")
                .header("Idempotency-Key", UUID.randomUUID().toString()).POST(json(Map.of(
                        "compte_source_id", rasoa, "beneficiaire_id", beneficiaire.required("id").stringValue(),
                        "montant", 1000)))), 201);
        var chemin = "/transferts/" + transfert.required("id").stringValue();

        for (var cle : List.of("andry", "agent")) {
            verifierProbleme(annuler(chemin, cle), 403, "INTERDIT");
        }
        assertThat(corps(get(chemin), 200).required("statut").stringValue()).isEqualTo("EN_ATTENTE");
        assertThat(corps(annuler(chemin, "rasoa"), 200).required("statut").stringValue()).isEqualTo("ANNULEE");
    }

    private HttpResponse<String> annuler(String chemin, String cle) throws Exception {
        return envoyer(requete(chemin + "/annulation").header("Authorization", "Bearer " + jeton(cle))
                .POST(HttpRequest.BodyPublishers.noBody()));
    }

    private static HttpRequest.BodyPublisher json(Map<String, ?> donnees) {
        return HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(donnees));
    }

    private HttpResponse<String> connexion(String identifiants) throws Exception {
        return envoyer(requete("/auth/token").header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(identifiants)));
    }
}
