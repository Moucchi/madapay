package com.ETU001979.madapay_rafidinjanahary_emmanuel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("tp1")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompteLectureConformiteTests {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final JsonNode COMPTES = lireComptes();

    @LocalServerPort
    private int port;

    @Test
    @Tag("socle")
    void listeDesComptesRenvoieUnePage() throws Exception {
        var reponse = get("/comptes");
        var page = corps(reponse, 200);

        assertThat(contentType(reponse)).startsWith("application/json");
        assertThat(page.required("page").longValue()).isEqualTo(1L);
        assertThat(page.required("taille").longValue()).isEqualTo(20L);
        assertThat(page.required("total").longValue()).isGreaterThanOrEqualTo(COMPTES.size());
        assertThat(page.required("elements").isArray()).isTrue();
    }

    @Test
    @Tag("socle")
    void listeContientLesComptesDuJeuDeDonnees() throws Exception {
        var page = corps(get("/comptes?taille=100"), 200);

        assertThat(page.required("elements"))
                .extracting(compte -> compte.required("telephone").stringValue())
                .containsAll(StreamSupport.stream(COMPTES.spliterator(), false)
                        .map(compte -> compte.required("telephone").stringValue())
                        .toList());
    }

    @Test
    @Tag("socle")
    void litUnCompte() throws Exception {
        var rasoa = compteDuJeuDeDonnees("rasoa");
        var recherche = corps(get("/comptes?telephone=" + rasoa.required("telephone").stringValue()), 200);
        var elements = recherche.required("elements");
        assertThat(elements).hasSize(1);
        var id = elements.get(0).required("id").stringValue();
        var compte = corps(get("/comptes/" + id), 200);

        assertThat(compte.required("id").stringValue()).isEqualTo(id);
        assertThat(compte.required("telephone")).isEqualTo(rasoa.required("telephone"));
        assertThat(compte.required("titulaire")).isEqualTo(rasoa.required("titulaire"));
        assertThat(compte.required("solde").isIntegralNumber()).isTrue();
        assertThat(compte.required("plafond_journalier").longValue()).isEqualTo(2_000_000L);
        compte.required("cree_le");
    }

    @Test
    @Tag("extension")
    void compteInconnuRenvoieUnProbleme404() throws Exception {
        verifierProbleme(get("/comptes/" + UUID.randomUUID()), 404, "INTROUVABLE");
    }

    @Test
    @Tag("extension")
    void routeInconnueRenvoieUnProbleme404() throws Exception {
        verifierProbleme(get("/cette-route-n-existe-pas"), 404, null);
    }

    private static void verifierProbleme(HttpResponse<String> reponse, int statut, String code) {
        var probleme = corps(reponse, statut);

        assertThat(contentType(reponse)).startsWith("application/problem+json");
        probleme.required("type");
        probleme.required("title");
        assertThat(probleme.required("status").intValue()).isEqualTo(statut);
        if (code != null) {
            assertThat(probleme.required("code").stringValue()).isEqualTo(code);
        }
    }

    private static JsonNode compteDuJeuDeDonnees(String cle) {
        return StreamSupport.stream(COMPTES.spliterator(), false)
                .filter(compte -> cle.equals(compte.required("cle").stringValue()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Compte absent du jeu de données : " + cle));
    }

    private static JsonNode lireComptes() {
        try (var entree = new ClassPathResource("contrat/seed.json").getInputStream()) {
            return JSON.readTree(entree).required("comptes");
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire le jeu de données", e);
        }
    }

    private HttpResponse<String> get(String chemin) throws IOException, InterruptedException {
        var requete = HttpRequest.newBuilder(URI.create("http://localhost:" + port + chemin))
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();
        return CLIENT.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    private static JsonNode corps(HttpResponse<String> reponse, int statut) {
        assertThat(reponse.statusCode()).as("Statut HTTP, corps %s", reponse.body()).isEqualTo(statut);
        return JSON.readTree(reponse.body());
    }

    private static String contentType(HttpResponse<String> reponse) {
        return reponse.headers().firstValue("Content-Type").orElse("");
    }
}
