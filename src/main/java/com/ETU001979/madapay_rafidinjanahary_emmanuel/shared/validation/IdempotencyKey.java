package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ValidationException;

/** En-tête Idempotency-Key du contrat : obligatoire, 8 à 64 caractères. */
public final class IdempotencyKey {

    public static final String EN_TETE = "Idempotency-Key";
    private static final int LONGUEUR_MIN = 8;
    private static final int LONGUEUR_MAX = 64;

    private IdempotencyKey() {
    }

    public static String exiger(String valeur) {
        if (valeur == null || valeur.isBlank()) {
            throw ValidationException.surChamp(EN_TETE, "L'en-tête " + EN_TETE + " est obligatoire.");
        }
        if (valeur.length() < LONGUEUR_MIN || valeur.length() > LONGUEUR_MAX) {
            throw ValidationException.surChamp(EN_TETE,
                    "L'en-tête " + EN_TETE + " doit contenir entre " + LONGUEUR_MIN + " et " + LONGUEUR_MAX + " caractères.");
        }
        return valeur;
    }
}
