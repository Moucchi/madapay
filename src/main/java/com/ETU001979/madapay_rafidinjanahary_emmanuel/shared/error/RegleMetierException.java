package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

/** Requête valide mais refusée par une règle métier : 422, avec un code du contrat. */
public abstract class RegleMetierException extends RuntimeException {

    private final Code code;

    protected RegleMetierException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }
}
