package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

public class LimiteDebitException extends RuntimeException {

    private final long delaiSecondes;

    public LimiteDebitException(long delaiSecondes) {
        super("Trop de tentatives de connexion pour ce numéro.");
        this.delaiSecondes = delaiSecondes;
    }

    public long delaiSecondes() {
        return delaiSecondes;
    }
}
