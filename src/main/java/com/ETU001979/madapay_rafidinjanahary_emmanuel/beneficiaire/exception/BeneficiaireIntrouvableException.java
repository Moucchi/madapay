package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.exception;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.RessourceIntrouvableException;

import java.util.UUID;

public class BeneficiaireIntrouvableException extends RessourceIntrouvableException {

    public BeneficiaireIntrouvableException(UUID id) {
        super("Aucun bénéficiaire ne porte l'identifiant " + id + ".");
    }
}
