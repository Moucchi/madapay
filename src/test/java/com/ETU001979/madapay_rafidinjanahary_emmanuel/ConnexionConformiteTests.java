package com.ETU001979.madapay_rafidinjanahary_emmanuel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("tp3")
class ConnexionConformiteTests extends ConformiteHttp {

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

    private HttpResponse<String> connexion(String identifiants) throws Exception {
        return envoyer(requete("/auth/token").header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(identifiants)));
    }
}
