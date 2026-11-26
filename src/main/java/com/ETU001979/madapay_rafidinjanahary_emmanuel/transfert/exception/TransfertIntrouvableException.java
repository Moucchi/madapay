package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.exception;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.RessourceIntrouvableException;

import java.util.UUID;

public class TransfertIntrouvableException extends RessourceIntrouvableException {

    public TransfertIntrouvableException(UUID id) {
        super("Aucun transfert ne porte l'identifiant " + id + ".");
    }
}
