package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.exception;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.Code;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.RegleMetierException;

public class SoldeInsuffisantException extends RegleMetierException {

    public SoldeInsuffisantException(long solde, long aDebiter) {
        super(Code.SOLDE_INSUFFISANT,
                "Le solde (" + solde + " Ar) ne couvre pas le montant et les frais (" + aDebiter + " Ar).");
    }
}
