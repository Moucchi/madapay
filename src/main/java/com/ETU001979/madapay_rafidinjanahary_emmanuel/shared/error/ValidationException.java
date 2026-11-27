package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<Erreur> erreurs;

    public ValidationException(String message, List<Erreur> erreurs) {
        super(message);
        this.erreurs = List.copyOf(erreurs);
    }

    public static ValidationException surChamp(String champ, String message) {
        return new ValidationException(message, List.of(new Erreur(champ, message)));
    }

    public List<Erreur> getErreurs() {
        return erreurs;
    }
}
