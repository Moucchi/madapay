package com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.LimiteDebitException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.NonAuthentifieException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LimiteurConnexionTests {

    private static final String TELEPHONE = "0321111111";
    private static final Instant DEBUT = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    void expireLesEchecsIndividuellementEtArronditLeDelaiALaSecondeSuperieure() {
        var horloge = mock(Clock.class);
        when(horloge.instant()).thenReturn(DEBUT);
        var limiteur = new LimiteurConnexion(horloge);
        verifierEchec(limiteur);
        when(horloge.instant()).thenReturn(DEBUT.plusSeconds(10));
        for (int i = 0; i < 4; i++) {
            verifierEchec(limiteur);
        }

        var connexionValide = UsernamePasswordAuthenticationToken.authenticated(TELEPHONE, null, List.of());
        when(horloge.instant()).thenReturn(DEBUT.plusMillis(59_900));
        assertThatExceptionOfType(LimiteDebitException.class)
                .isThrownBy(() -> limiteur.authentifier(TELEPHONE, () -> connexionValide))
                .satisfies(erreur -> assertThat(erreur.delaiSecondes()).isEqualTo(1));

        when(horloge.instant()).thenReturn(DEBUT.plusSeconds(60));
        verifierEchec(limiteur);
        assertThatExceptionOfType(LimiteDebitException.class)
                .isThrownBy(() -> limiteur.authentifier(TELEPHONE, () -> connexionValide))
                .satisfies(erreur -> assertThat(erreur.delaiSecondes()).isEqualTo(10));

        when(horloge.instant()).thenReturn(DEBUT.plusSeconds(70));
        assertThat(limiteur.authentifier(TELEPHONE, () -> connexionValide)).isSameAs(connexionValide);
    }

    @Test
    void appliqueLeSeuilAuxTentativesConcurrentes() throws Exception {
        var limiteur = new LimiteurConnexion(Clock.fixed(DEBUT, ZoneOffset.UTC));
        var depart = new CountDownLatch(1);
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var tentatives = new ArrayList<Future<Class<?>>>();
            for (int i = 0; i < 8; i++) {
                tentatives.add(executor.submit(() -> {
                    depart.await();
                    try {
                        limiteur.authentifier(TELEPHONE, LimiteurConnexionTests::identifiantsIncorrects);
                    } catch (NonAuthentifieException | LimiteDebitException e) {
                        return e.getClass();
                    }
                    throw new AssertionError("Les identifiants sont incorrects.");
                }));
            }
            depart.countDown();

            var erreurs = new ArrayList<Class<?>>();
            for (var tentative : tentatives) {
                erreurs.add(tentative.get());
            }
            assertThat(erreurs).containsExactlyInAnyOrder(
                    NonAuthentifieException.class, NonAuthentifieException.class, NonAuthentifieException.class,
                    NonAuthentifieException.class, NonAuthentifieException.class,
                    LimiteDebitException.class, LimiteDebitException.class, LimiteDebitException.class);
        }
    }

    private static void verifierEchec(LimiteurConnexion limiteur) {
        assertThatExceptionOfType(NonAuthentifieException.class)
                .isThrownBy(() -> limiteur.authentifier(TELEPHONE, LimiteurConnexionTests::identifiantsIncorrects));
    }

    private static Authentication identifiantsIncorrects() {
        throw new BadCredentialsException("Identifiants incorrects.");
    }
}
