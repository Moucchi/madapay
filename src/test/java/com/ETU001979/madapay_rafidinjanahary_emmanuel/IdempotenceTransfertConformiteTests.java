package com.ETU001979.madapay_rafidinjanahary_emmanuel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("tp3")
@Tag("extension")
class IdempotenceTransfertConformiteTests extends ConformiteHttp {

    @Test
    void rejoueLeTransfertInitialApresSonAnnulation() throws Exception {
        var compte = compteId("hery");
        var beneficiaire = creerBeneficiaire(compte, "0348999011");
        var cle = UUID.randomUUID().toString();
        var avant = solde(compte);
        var creation = corps(creerTransfert(compte, beneficiaire, 2000, cle), 201);
        var transfert = "/transferts/" + creation.required("id").stringValue();

        var annule = corps(envoyer(requete(transfert + "/annulation")
                .POST(HttpRequest.BodyPublishers.noBody())), 200);
        assertThat(annule.required("statut").stringValue()).isEqualTo("ANNULEE");
        assertThat(solde(compte)).isEqualTo(avant);

        var rejeu = creerTransfert(compte, beneficiaire, 2000, cle);

        assertThat(rejeu.headers().firstValue("Idempotency-Replayed")).contains("true");
        assertThat(corps(rejeu, 200)).isEqualTo(creation);
        assertThat(solde(compte)).isEqualTo(avant);
    }

    @Test
    void uneCreationEchoueePermetDeRetenterAvecLaMemeCle() throws Exception {
        var compte = compteId("hery");
        var beneficiaire = creerBeneficiaire(compte, "0348999012");
        var cle = UUID.randomUUID().toString();
        var avant = solde(compte);

        verifierProbleme(creerTransfert(compte, beneficiaire, 99, cle), 422, "MONTANT_TROP_FAIBLE");
        assertThat(solde(compte)).isEqualTo(avant);

        var reponse = creerTransfert(compte, beneficiaire, 1000, cle);

        assertThat(corps(reponse, 201).required("montant").longValue()).isEqualTo(1000);
        assertThat(reponse.headers().firstValue("Idempotency-Replayed")).isEmpty();
        assertThat(avant - solde(compte)).isEqualTo(1000);
    }

    private String creerBeneficiaire(String compte, String telephone) throws IOException, InterruptedException {
        var demande = requete("/comptes/" + compte + "/beneficiaires")
                .header("Content-Type", "application/json")
                .POST(json(Map.of("nom", "Bénéficiaire idempotence", "telephone", telephone)));
        return corps(envoyer(demande), 201).required("id").stringValue();
    }

    private HttpResponse<String> creerTransfert(String compte, String beneficiaire, int montant, String cle)
            throws IOException, InterruptedException {
        return envoyer(requete("/transferts").header("Content-Type", "application/json")
                .header("Idempotency-Key", cle)
                .POST(json(Map.of("compte_source_id", compte, "beneficiaire_id", beneficiaire,
                        "montant", montant))));
    }

    private long solde(String compte) throws IOException, InterruptedException {
        return corps(get("/comptes/" + compte), 200).required("solde").longValue();
    }

    private static HttpRequest.BodyPublisher json(Map<String, ?> donnees) {
        return HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(donnees));
    }
}
