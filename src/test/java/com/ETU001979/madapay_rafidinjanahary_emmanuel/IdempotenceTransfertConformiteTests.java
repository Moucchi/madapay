package com.ETU001979.madapay_rafidinjanahary_emmanuel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("tp3")
class IdempotenceTransfertConformiteTests extends ConformiteHttp {

    @Test
    @Tag("extension")
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
    @Tag("extension")
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

    @Test
    @Tag("defi")
    void debiteUneSeuleFoisPourHuitRequetesConcurrentes() throws Exception {
        var compte = compteId("hery");
        var beneficiaire = creerBeneficiaire(compte, "0348999014");
        var cle = UUID.randomUUID().toString();
        var avant = solde(compte);

        var reponses = executerSimultanement(8, () -> creerTransfert(compte, beneficiaire, 10_000, cle));

        assertThat(reponses).extracting(HttpResponse::statusCode)
                .containsExactlyInAnyOrder(201, 200, 200, 200, 200, 200, 200, 200);
        var creation = reponses.stream().filter(reponse -> reponse.statusCode() == 201).findFirst().orElseThrow();
        var transfert = corps(creation, 201);
        for (var rejeu : reponses) {
            if (rejeu.statusCode() == 200) {
                assertThat(rejeu.headers().firstValue("Idempotency-Replayed")).contains("true");
                assertThat(corps(rejeu, 200)).isEqualTo(transfert);
            }
        }
        assertThat(avant - solde(compte)).isEqualTo(transfert.required("total_debite").longValue());
    }

    @Test
    @Tag("defi")
    void rembourseUneSeuleFoisPourSixAnnulationsConcurrentes() throws Exception {
        var compte = compteId("hery");
        var beneficiaire = creerBeneficiaire(compte, "0348999015");
        var creation = corps(creerTransfert(compte, beneficiaire, 30_000, UUID.randomUUID().toString()), 201);
        var id = creation.required("id").stringValue();
        var montantAttendu = creation.required("total_debite").longValue();
        var avant = solde(compte);

        var reponses = executerSimultanement(6, () -> envoyer(requete("/transferts/" + id + "/annulation")
                .POST(HttpRequest.BodyPublishers.noBody())));

        assertThat(reponses).extracting(HttpResponse::statusCode)
                .containsExactlyInAnyOrder(200, 409, 409, 409, 409, 409);
        for (var reponse : reponses) {
            if (reponse.statusCode() == 409) {
                verifierProbleme(reponse, 409, "TRANSFERT_NON_ANNULABLE");
            } else {
                assertThat(corps(reponse, 200).required("statut").stringValue()).isEqualTo("ANNULEE");
            }
        }
        assertThat(solde(compte) - avant).isEqualTo(montantAttendu);
        var historique = corps(get("/comptes/" + compte + "/transactions?taille=100"), 200);
        assertThat(historique.required("elements"))
                .filteredOn(ecriture -> id.equals(ecriture.path("transfert_id").stringValue())
                        && "REMBOURSEMENT".equals(ecriture.required("type").stringValue()))
                .singleElement()
                .satisfies(ecriture -> assertThat(ecriture.required("montant").longValue()).isEqualTo(montantAttendu));
    }

    private static List<HttpResponse<String>> executerSimultanement(int nombre,
            Callable<HttpResponse<String>> demande) throws Exception {
        var depart = new CyclicBarrier(nombre);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var attentes = new ArrayList<Future<HttpResponse<String>>>();
            for (int i = 0; i < nombre; i++) {
                attentes.add(executor.submit(() -> {
                    depart.await(5, TimeUnit.SECONDS);
                    return demande.call();
                }));
            }
            var reponses = new ArrayList<HttpResponse<String>>();
            for (var attente : attentes) {
                reponses.add(attente.get());
            }
            return reponses;
        }
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
