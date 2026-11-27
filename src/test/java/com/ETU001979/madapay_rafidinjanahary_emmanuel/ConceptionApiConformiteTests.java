package com.ETU001979.madapay_rafidinjanahary_emmanuel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    @Test
    @Tag("socle")
    void accepteLesQuatrePrefixesMalgaches() throws Exception {
        var base = beneficiaires("andry");
        for (var prefixe : List.of("032", "033", "034", "038")) {
            var reponse = post(base, Map.of("nom", "Préfixe " + prefixe, "telephone", prefixe + "1234567"));
            assertThat(reponse.statusCode()).as("Préfixe %s, corps %s", prefixe, reponse.body())
                    .isIn(201, 409);
        }
    }

    @Test
    @Tag("socle")
    void doublonDeTelephoneRenvoie409() throws Exception {
        var base = beneficiaires("rasoa");
        var telephone = prochainTelephone();

        corps(post(base, Map.of("nom", "Premier", "telephone", telephone)), 201);
        verifierProbleme(post(base, Map.of("nom", "Second", "telephone", telephone)), 409, "CONFLIT");
    }

    @Test
    @Tag("extension")
    void transfertDebiteLeMontantEtLesFrais() throws Exception {
        var avant = solde("rasoa");
        var reponse = creerTransfert("rasoa", 5000);
        var transfert = corps(reponse, 201);

        assertThat(montant(transfert, "montant")).isEqualTo(5000);
        assertThat(montant(transfert, "frais")).isEqualTo(75);
        assertThat(montant(transfert, "total_debite")).isEqualTo(5075);
        assertThat(texte(transfert, "statut")).isEqualTo("EN_ATTENTE");
        assertThat(texte(transfert, "compte_source_id")).isEqualTo(compteId("rasoa"));
        verifierLocation(reponse, "/transferts/" + texte(transfert, "id"));
        assertThat(avant - solde("rasoa")).isEqualTo(5075);
    }

    @ParameterizedTest
    @Tag("extension")
    @CsvSource({"100, 0", "1000, 0", "1001, 50", "2000, 50", "10000, 150", "33333, 500", "100000, 1500"})
    void calculeLesFraisParPalier(int montant, int frais) throws Exception {
        var transfert = corps(creerTransfert("hery", montant), 201);

        assertThat(montant(transfert, "frais")).as("Frais pour %s Ar", montant).isEqualTo(frais);
        assertThat(montant(transfert, "total_debite")).isEqualTo(montant + frais);
    }

    @Test
    @Tag("extension")
    void litUnTransfert() throws Exception {
        var transfert = corps(creerTransfert("hery", 2500), 201);
        var lu = corps(get("/transferts/" + texte(transfert, "id")), 200);

        for (var champ : List.of("id", "montant", "frais", "total_debite", "statut",
                "compte_source_id", "beneficiaire_id")) {
            assertThat(lu.required(champ)).as("Champ %s", champ).isEqualTo(transfert.required(champ));
        }
        verifierProbleme(get("/transferts/" + UUID.randomUUID()), 404, "INTROUVABLE");
    }

    @Test
    @Tag("extension")
    void soldeInsuffisantRenvoie422SansDebiter() throws Exception {
        var avant = solde("miora");

        verifierProbleme(creerTransfert("miora", 4990), 422, "SOLDE_INSUFFISANT");
        assertThat(solde("miora")).isEqualTo(avant);
    }

    @ParameterizedTest
    @Tag("extension")
    @ValueSource(ints = {1, 50, 99})
    void montantSousLeMinimumRenvoie422(int montant) throws Exception {
        verifierProbleme(creerTransfert("hery", montant), 422, "MONTANT_TROP_FAIBLE");
    }

    @ParameterizedTest
    @Tag("extension")
    @ValueSource(strings = {"0", "-500", "\"abc\"", "12.5", "null"})
    void montantInvalideRenvoie400(String montantJson) throws Exception {
        var beneficiaire = creerBeneficiaire("hery");
        var donnees = JSON.createObjectNode()
                .put("compte_source_id", compteId("hery"))
                .put("beneficiaire_id", texte(beneficiaire, "id"));
        donnees.set("montant", JSON.readTree(montantJson));

        verifierProbleme(postTransfert(donnees), 400, "VALIDATION");
    }

    @Test
    @Tag("extension")
    void cleIdempotenceObligatoire() throws Exception {
        var beneficiaire = creerBeneficiaire("hery");
        var donnees = Map.of("compte_source_id", compteId("hery"),
                "beneficiaire_id", texte(beneficiaire, "id"), "montant", 1000);

        verifierProbleme(post("/transferts", donnees), 400, "VALIDATION");
    }

    @Test
    @Tag("extension")
    void beneficiaireOuCompteInconnuRenvoie404() throws Exception {
        var beneficiaire = creerBeneficiaire("andry");
        var donnees = JSON.createObjectNode()
                .put("compte_source_id", compteId("hery"))
                .put("beneficiaire_id", texte(beneficiaire, "id"))
                .put("montant", 1000);

        verifierProbleme(postTransfert(donnees), 404, "INTROUVABLE");
        donnees.put("compte_source_id", UUID.randomUUID().toString());
        verifierProbleme(postTransfert(donnees), 404, "INTROUVABLE");
    }

    @Test
    @Tag("extension")
    void historiqueContientLeDebitEtLesFrais() throws Exception {
        var transfert = corps(creerTransfert("hery", 20000), 201);
        var transactions = transactionsDuTransfert(transfert, "hery").toList();

        assertThat(transactions).extracting(transaction -> texte(transaction, "type")).contains("DEBIT", "FRAIS");
        var debit = transactions.stream().filter(transaction -> "DEBIT".equals(texte(transaction, "type")))
                .findFirst().orElseThrow();
        var frais = transactions.stream().filter(transaction -> "FRAIS".equals(texte(transaction, "type")))
                .findFirst().orElseThrow();
        assertThat(montant(debit, "montant")).isEqualTo(20000);
        assertThat(montant(frais, "montant")).isEqualTo(300);
        assertThat(texte(debit, "compte_id")).isEqualTo(compteId("hery"));
    }

    @Test
    @Tag("extension")
    void historiquePagineEtTrieParDateDecroissante() throws Exception {
        for (int i = 0; i < 3; i++) {
            corps(creerTransfert("hery", 1000), 201);
        }
        var page = corps(historique("hery", "taille=2"), 200);
        assertThat(montant(page, "taille")).isEqualTo(2);
        assertThat(elementsPage(page)).hasSize(2);
        assertThat(montant(page, "total")).isGreaterThanOrEqualTo(3);

        assertThat(elementsPage(corps(historique("hery", "taille=100"), 200)))
                .extracting(transaction -> OffsetDateTime.parse(texte(transaction, "date")).toInstant())
                .isSortedAccordingTo(Comparator.reverseOrder());
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

    private long solde(String cle) throws IOException, InterruptedException {
        return montant(corps(get("/comptes/" + compteId(cle)), 200), "solde");
    }

    private HttpResponse<String> creerTransfert(String cle, int montant)
            throws IOException, InterruptedException {
        var beneficiaire = creerBeneficiaire(cle);
        return postTransfert(JSON.createObjectNode()
                .put("compte_source_id", compteId(cle))
                .put("beneficiaire_id", texte(beneficiaire, "id"))
                .put("montant", montant)
                .put("motif", "Test"));
    }

    private HttpResponse<String> postTransfert(JsonNode donnees) throws IOException, InterruptedException {
        return envoyer(requete("/transferts").header("Content-Type", "application/json")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(donnees))));
    }

    private static long montant(JsonNode noeud, String champ) {
        var valeur = noeud.required(champ);
        assertThat(valeur.isIntegralNumber()).as("Champ entier %s", champ).isTrue();
        return valeur.longValue();
    }

    private HttpResponse<String> historique(String cle, String parametres)
            throws IOException, InterruptedException {
        return get("/comptes/" + compteId(cle) + "/transactions?" + parametres);
    }

    private Stream<JsonNode> transactionsDuTransfert(JsonNode transfert, String cle)
            throws IOException, InterruptedException {
        var elements = elementsPage(corps(historique(cle, "taille=100"), 200));
        var id = texte(transfert, "id");
        return StreamSupport.stream(elements.spliterator(), false)
                .filter(transaction -> id.equals(transaction.path("transfert_id").stringValue()));
    }
}
