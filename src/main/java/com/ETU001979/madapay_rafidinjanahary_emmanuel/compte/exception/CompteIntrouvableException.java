package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.exception;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.RessourceIntrouvableException;

import java.util.UUID;

public class CompteIntrouvableException extends RessourceIntrouvableException {

    public CompteIntrouvableException(UUID id) {
        super("Aucun compte ne porte l'identifiant " + id + ".");
    }
}
