package com.ETU001979.madapay_rafidinjanahary_emmanuel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("tp3")
@Tag("extension")
class VersionBeneficiaireConformiteTests extends ConformiteHttp {

    @Test
    void uneSeuleModificationConcurrentePeutUtiliserLaVersionInitiale() throws Exception {
        var base = "/comptes/" + compteId("rasoa") + "/beneficiaires";
        var creation = envoyer(requete(base).header("Content-Type", "application/json")
                .POST(json(Map.of("nom", "Nom initial", "telephone", "0388999013"))));
        var chemin = base + "/" + corps(creation, 201).required("id").stringValue();
        var version = creation.headers().firstValue("ETag").orElseThrow();
        assertThat(get(chemin).headers().firstValue("ETag")).contains(version);
        verifierProbleme(modifier(chemin, "W/" + version, "Version faible"), 412, "VERSION_OBSOLETE");

        var depart = new CountDownLatch(1);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var premiere = executor.submit(() -> {
                depart.await();
                return modifier(chemin, version, "Premier nom");
            });
            var deuxieme = executor.submit(() -> {
                depart.await();
                return modifier(chemin, version, "Second nom");
            });
            depart.countDown();
            var reponses = List.of(premiere.get(), deuxieme.get());

            assertThat(reponses).extracting(HttpResponse::statusCode).containsExactlyInAnyOrder(200, 412);
            var refusee = reponses.stream().filter(r -> r.statusCode() == 412).findFirst().orElseThrow();
            verifierProbleme(refusee, 412, "VERSION_OBSOLETE");
            var acceptee = reponses.stream().filter(r -> r.statusCode() == 200).findFirst().orElseThrow();
            var courante = get(chemin);
            assertThat(corps(courante, 200)).isEqualTo(corps(acceptee, 200));
            var versionCourante = courante.headers().firstValue("ETag").orElseThrow();
            assertThat(acceptee.headers().firstValue("ETag")).contains(versionCourante);
            assertThat(versionCourante).isNotEqualTo(version);
        }
    }

    private HttpResponse<String> modifier(String chemin, String version, String nom)
            throws IOException, InterruptedException {
        return envoyer(requete(chemin).header("Content-Type", "application/merge-patch+json")
                .header("If-Match", version).method("PATCH", json(Map.of("nom", nom))));
    }

    private static HttpRequest.BodyPublisher json(Map<String, String> donnees) {
        return HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(donnees));
    }
}
