package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

public class NonAuthentifieException extends RuntimeException {

    public NonAuthentifieException() {
        super("Identifiants incorrects.");
    }
}
