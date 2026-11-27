package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

public abstract class ConflitException extends RuntimeException {

    private final Code code;

    protected ConflitException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public Code getCode() {
        return code;
    }
}
