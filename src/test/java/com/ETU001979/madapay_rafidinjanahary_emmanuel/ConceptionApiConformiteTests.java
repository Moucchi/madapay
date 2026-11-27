package com.ETU001979.madapay_rafidinjanahary_emmanuel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("tp2")
@Execution(ExecutionMode.SAME_THREAD)
class ConceptionApiConformiteTests extends ConformiteHttp {

    private static final AtomicInteger PROCHAIN_TELEPHONE = new AtomicInteger(32_800_000);

    @Test
    @Tag("socle")
    void creeUnBeneficiaire() throws Exception {
        var base = beneficiaires("rasoa");
        var telephone = prochainTelephone();
        var reponse = post(base, Map.of("nom", "Rakoto Andry", "telephone", telephone));
        var beneficiaire = corps(reponse, 201);

        assertThat(texte(beneficiaire, "nom")).isEqualTo("Rakoto Andry");
        assertThat(texte(beneficiaire, "telephone")).isEqualTo(telephone);
        assertThat(texte(beneficiaire, "compte_id")).isEqualTo(compteId("rasoa"));
        beneficiaire.required("cree_le");
        verifierLocation(reponse, base + "/" + texte(beneficiaire, "id"));
    }

    @Test
    @Tag("socle")
    void litEtListeLesBeneficiaires() throws Exception {
        var beneficiaire = creerBeneficiaire("rasoa");
        var base = beneficiaires("rasoa");
        var id = texte(beneficiaire, "id");

        assertThat(corps(get(base + "/" + id), 200)).isEqualTo(beneficiaire);
        var page = corps(get(base + "?taille=100"), 200);
        assertThat(elementsPage(page)).extracting(element -> texte(element, "id")).contains(id);
    }

    private String beneficiaires(String cle) throws IOException, InterruptedException {
        return "/comptes/" + compteId(cle) + "/beneficiaires";
    }

    private static String prochainTelephone() {
        return "0" + PROCHAIN_TELEPHONE.getAndIncrement();
    }

    private HttpResponse<String> post(String chemin, Map<String, ?> donnees)
            throws IOException, InterruptedException {
        return envoyer(requete(chemin).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(donnees))));
    }

    private static String texte(JsonNode noeud, String champ) {
        return noeud.required(champ).stringValue();
    }

    private static void verifierLocation(HttpResponse<String> reponse, String chemin) {
        assertThat(reponse.headers().firstValue("Location").orElse("")).endsWith(chemin);
    }

    private JsonNode creerBeneficiaire(String cle) throws IOException, InterruptedException {
        return corps(post(beneficiaires(cle),
                Map.of("nom", "Bénéficiaire test", "telephone", prochainTelephone())), 201);
    }

    private static JsonNode elementsPage(JsonNode page) {
        page.required("page");
        page.required("taille");
        page.required("total");
        var elements = page.required("elements");
        assertThat(elements.isArray()).isTrue();
        return elements;
    }
}
