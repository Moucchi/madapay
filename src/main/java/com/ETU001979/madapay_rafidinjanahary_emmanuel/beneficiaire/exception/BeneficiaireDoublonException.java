package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.exception;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.Code;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ConflitException;

public class BeneficiaireDoublonException extends ConflitException {

    public BeneficiaireDoublonException(String telephone) {
        super(Code.CONFLIT, "Un bénéficiaire avec le numéro " + telephone + " existe déjà pour ce compte.");
    }
}
