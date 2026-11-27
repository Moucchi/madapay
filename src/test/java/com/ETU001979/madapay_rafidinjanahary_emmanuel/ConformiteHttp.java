package com.ETU001979.madapay_rafidinjanahary_emmanuel;

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
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "JWT_SECRET=" + ConformiteHttp.SECRET_JWT_TEST)
abstract class ConformiteHttp {

    static final String SECRET_JWT_TEST = "secret-reserve-aux-tests-madapay-tp3";

    static final JsonMapper JSON = JsonMapper.builder().build();

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    static final JsonNode COMPTES = lireComptes();

    @LocalServerPort
    private int port;

    private String jetonAdmin;

    static JsonNode verifierProbleme(HttpResponse<String> reponse, int statut, String code) {
        var probleme = corps(reponse, statut);

        assertThat(contentType(reponse)).startsWith("application/problem+json");
        probleme.required("type");
        probleme.required("title");
        assertThat(probleme.required("status").intValue()).isEqualTo(statut);
        if (code != null) {
            assertThat(probleme.required("code").stringValue()).isEqualTo(code);
        }
        return probleme;
    }

    static JsonNode compteDuJeuDeDonnees(String cle) {
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

    HttpResponse<String> get(String chemin) throws IOException, InterruptedException {
        return envoyer(requete(chemin).GET());
    }

    HttpRequest.Builder requete(String chemin) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + port + chemin))
                .timeout(Duration.ofSeconds(15));
    }

    HttpResponse<String> envoyer(HttpRequest.Builder requete) throws IOException, InterruptedException {
        var demande = requete.build();
        if (!"/auth/token".equals(demande.uri().getPath())
                && demande.headers().firstValue("Authorization").isEmpty()) {
            if (jetonAdmin == null) {
                jetonAdmin = jeton("admin");
            }
            requete.header("Authorization", "Bearer " + jetonAdmin);
        }
        return envoyerSansAuthentification(requete);
    }

    HttpResponse<String> envoyerSansAuthentification(HttpRequest.Builder requete)
            throws IOException, InterruptedException {
        return CLIENT.send(requete.build(), HttpResponse.BodyHandlers.ofString());
    }

    String jeton(String cle) throws IOException, InterruptedException {
        var compte = compteDuJeuDeDonnees(cle);
        var identifiants = JSON.createObjectNode()
                .set("telephone", compte.required("telephone"))
                .set("code_pin", compte.required("code_pin"));
        var connexion = requete("/auth/token").header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(identifiants)));
        return corps(envoyerSansAuthentification(connexion), 200).required("access_token").stringValue();
    }

    String compteId(String cle) throws IOException, InterruptedException {
        var telephone = compteDuJeuDeDonnees(cle).required("telephone").stringValue();
        var elements = corps(get("/comptes?telephone=" + telephone), 200).required("elements");
        assertThat(elements).as("Compte %s", cle).hasSize(1);
        return elements.get(0).required("id").stringValue();
    }

    static JsonNode corps(HttpResponse<String> reponse, int statut) {
        assertThat(reponse.statusCode()).as("Statut HTTP, corps %s", reponse.body()).isEqualTo(statut);
        return JSON.readTree(reponse.body());
    }

    static String contentType(HttpResponse<String> reponse) {
        return reponse.headers().firstValue("Content-Type").orElse("");
    }
}
