package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.exception;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.Code;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ConflitException;

public class IdempotenceConflitException extends ConflitException {

    public IdempotenceConflitException() {
        super(Code.IDEMPOTENCE_CONFLIT, "La clé d'idempotence a déjà servi avec un corps différent.");
    }
}
