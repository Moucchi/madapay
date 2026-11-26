package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation;

import java.util.regex.Pattern;

/** Règle du contrat sur les numéros : 10 chiffres, préfixes 032, 033, 034 ou 038. */
public final class Telephone {

    public static final String REGEX = "^03[2348][0-9]{7}$";
    public static final String MESSAGE =
            "Le numéro doit comporter 10 chiffres et commencer par 032, 033, 034 ou 038.";

    private static final Pattern MOTIF = Pattern.compile(REGEX);

    private Telephone() {
    }

    public static boolean estValide(Object valeur) {
        return valeur instanceof String s && MOTIF.matcher(s).matches();
    }
}
