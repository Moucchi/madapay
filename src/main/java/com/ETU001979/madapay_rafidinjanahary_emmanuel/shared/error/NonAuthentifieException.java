package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

public class NonAuthentifieException extends RuntimeException {

    public NonAuthentifieException() {
        this("Identifiants incorrects.");
    }

    public NonAuthentifieException(String message) {
        super(message);
    }
}
