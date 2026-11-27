package com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.LimiteDebitException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.NonAuthentifieException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Component
class LimiteurConnexion {

    private static final int MAX_ECHECS = 5;
    private static final Duration FENETRE = Duration.ofSeconds(60);

    private final Clock horloge;
    private final ConcurrentHashMap<String, Deque<Instant>> echecsParTelephone = new ConcurrentHashMap<>();
    private final AtomicReference<Instant> prochainNettoyage = new AtomicReference<>(Instant.MIN);

    LimiteurConnexion(Clock horloge) {
        this.horloge = horloge;
    }

    Authentication authentifier(String telephone, Supplier<Authentication> tentative) {
        nettoyerSiNecessaire(horloge.instant());
        var resultat = new AtomicReference<Authentication>();

        echecsParTelephone.compute(telephone, (numero, echecs) -> {
            var maintenant = horloge.instant();
            var dates = echecs == null ? new ArrayDeque<Instant>() : echecs;
            retirerEchecsExpires(dates, maintenant);
            if (dates.size() >= MAX_ECHECS) {
                var attente = Duration.between(maintenant, dates.getFirst().plus(FENETRE));
                long secondes = attente.toSeconds() + (attente.getNano() == 0 ? 0 : 1);
                throw new LimiteDebitException(secondes);
            }

            try {
                resultat.set(tentative.get());
            } catch (BadCredentialsException e) {
                dates.addLast(horloge.instant());
            }
            return dates.isEmpty() ? null : dates;
        });

        if (resultat.get() == null) {
            throw new NonAuthentifieException();
        }
        return resultat.get();
    }

    private void nettoyerSiNecessaire(Instant maintenant) {
        var echeance = prochainNettoyage.get();
        if (maintenant.isBefore(echeance)
                || !prochainNettoyage.compareAndSet(echeance, maintenant.plus(FENETRE))) {
            return;
        }
        echecsParTelephone.forEach((numero, ignore) ->
                echecsParTelephone.computeIfPresent(numero, (cle, echecs) -> {
                    retirerEchecsExpires(echecs, maintenant);
                    return echecs.isEmpty() ? null : echecs;
                }));
    }

    private static void retirerEchecsExpires(Deque<Instant> echecs, Instant maintenant) {
        while (!echecs.isEmpty() && !echecs.getFirst().plus(FENETRE).isAfter(maintenant)) {
            echecs.removeFirst();
        }
    }
}
