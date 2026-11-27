package com.ETU001979.madapay_rafidinjanahary_emmanuel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;
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

    @Test
    @Tag("socle")
    void beneficiaireInconnuRenvoie404() throws Exception {
        verifierProbleme(get(beneficiaires("rasoa") + "/" + UUID.randomUUID()), 404, "INTROUVABLE");
    }

    @Test
    @Tag("socle")
    void modifiePartiellementUnBeneficiaire() throws Exception {
        var beneficiaire = creerBeneficiaire("rasoa");
        var chemin = beneficiaires("rasoa") + "/" + texte(beneficiaire, "id");

        var modifie = corps(patch(chemin, Map.of("nom", "Nouveau nom")), 200);

        assertThat(texte(modifie, "nom")).isEqualTo("Nouveau nom");
        assertThat(texte(modifie, "telephone")).isEqualTo(texte(beneficiaire, "telephone"));
        assertThat(texte(corps(get(chemin), 200), "nom")).isEqualTo("Nouveau nom");
    }

    @Test
    @Tag("socle")
    void supprimeUnBeneficiaire() throws Exception {
        var beneficiaire = creerBeneficiaire("rasoa");
        var chemin = beneficiaires("rasoa") + "/" + texte(beneficiaire, "id");

        var reponse = envoyer(requete(chemin).DELETE());

        assertThat(reponse.statusCode()).isEqualTo(204);
        assertThat(reponse.body()).isEmpty();
        verifierProbleme(get(chemin), 404, "INTROUVABLE");
        verifierProbleme(envoyer(requete(chemin).DELETE()), 404, "INTROUVABLE");
    }

    @Test
    @Tag("socle")
    void creationSurUnCompteInconnuRenvoie404() throws Exception {
        var reponse = post("/comptes/" + UUID.randomUUID() + "/beneficiaires",
                Map.of("nom", "Test", "telephone", prochainTelephone()));

        verifierProbleme(reponse, 404, "INTROUVABLE");
    }

    @ParameterizedTest
    @Tag("socle")
    @ValueSource(strings = {"0361234567", "032123456", "03212345678", "+261321234567", "abcdefghij", ""})
    void telephoneInvalideRenvoie400AvecLeChamp(String telephone) throws Exception {
        var reponse = post(beneficiaires("rasoa"), Map.of("nom", "Test", "telephone", telephone));

        verifierChampsInvalides(reponse, "telephone");
    }

    @Test
    @Tag("socle")
    void refuseLeNomTropCourtEtLesChampsManquants() throws Exception {
        var base = beneficiaires("rasoa");

        verifierChampsInvalides(post(base, Map.of("nom", "A", "telephone", prochainTelephone())), "nom");
        verifierChampsInvalides(post(base, Map.of()), "nom", "telephone");
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

    private HttpResponse<String> patch(String chemin, Map<String, ?> donnees)
            throws IOException, InterruptedException {
        return envoyer(requete(chemin).header("Content-Type", "application/merge-patch+json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(donnees))));
    }

    private static void verifierChampsInvalides(HttpResponse<String> reponse, String... champs) {
        var probleme = verifierProbleme(reponse, 400, "VALIDATION");
        assertThat(probleme.required("erreurs"))
                .extracting(erreur -> texte(erreur, "champ"))
                .contains(champs);
    }
}
