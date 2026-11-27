package com.ETU001979.madapay_rafidinjanahary_emmanuel;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("tp1")
class CompteLectureConformiteTests extends ConformiteHttp {

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
        var id = compteId("rasoa");
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

    @Test
    @Tag("defi")
    void filtreParTelephone() throws Exception {
        var hery = compteDuJeuDeDonnees("hery");
        var page = corps(get("/comptes?telephone=" + hery.required("telephone").stringValue()), 200);

        assertThat(page.required("total").longValue()).isEqualTo(1L);
        assertThat(page.required("elements"))
                .extracting(compte -> compte.required("titulaire"))
                .containsExactly(hery.required("titulaire"));
    }

    @Test
    @Tag("defi")
    void telephoneInconnuRenvoieUnePageVide() throws Exception {
        var page = corps(get("/comptes?telephone=0329999999"), 200);

        assertThat(page.required("total").longValue()).isZero();
        var elements = page.required("elements");
        assertThat(elements.isArray()).isTrue();
        assertThat(elements).isEmpty();
    }

    @Test
    @Tag("defi")
    void pagineParPageEtTaille() throws Exception {
        var premiere = corps(get("/comptes?page=1&taille=2"), 200);
        var deuxieme = corps(get("/comptes?page=2&taille=2"), 200);

        assertThat(premiere.required("taille").intValue()).isEqualTo(2);
        assertThat(deuxieme.required("page").intValue()).isEqualTo(2);
        assertThat(premiere.required("total").longValue()).isGreaterThanOrEqualTo(COMPTES.size());
        var premiersComptes = premiere.required("elements");
        var comptesSuivants = deuxieme.required("elements");
        assertThat(premiersComptes).hasSize(2);
        assertThat(comptesSuivants).hasSize(2);
        assertThat(premiersComptes)
                .extracting(compte -> compte.required("id"))
                .doesNotContainAnyElementsOf(StreamSupport.stream(comptesSuivants.spliterator(), false)
                        .map(compte -> compte.required("id"))
                        .toList());
    }

    @ParameterizedTest
    @CsvSource({"taille, 0", "taille, 101", "page, 0", "page, -1"})
    @Tag("defi")
    void refuseLesBornesDePagination(String parametre, int valeur) throws Exception {
        verifierProbleme(get("/comptes?" + parametre + "=" + valeur), 400, "VALIDATION");
    }
}
