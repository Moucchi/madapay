package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

public class VersionObsoleteException extends RuntimeException {

    public VersionObsoleteException() {
        super("L'en-tête If-Match ne correspond pas à la version courante.");
    }
}
