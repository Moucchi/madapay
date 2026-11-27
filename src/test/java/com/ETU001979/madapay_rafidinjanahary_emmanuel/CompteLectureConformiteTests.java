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
