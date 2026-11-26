package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

/** Ressource inexistante : 404, code INTROUVABLE. Chaque module en dérive sa propre exception. */
public abstract class RessourceIntrouvableException extends RuntimeException {

    protected RessourceIntrouvableException(String message) {
        super(message);
    }
}
