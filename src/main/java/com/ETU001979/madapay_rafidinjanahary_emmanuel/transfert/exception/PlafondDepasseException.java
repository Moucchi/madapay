package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.exception;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.Code;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.RegleMetierException;

public class PlafondDepasseException extends RegleMetierException {

    public PlafondDepasseException(long dejaTransfere, long montant, long plafond) {
        super(Code.PLAFOND_DEPASSE,
                "Le cumul du jour (" + dejaTransfere + " Ar) plus ce transfert (" + montant
                        + " Ar) dépasse le plafond journalier (" + plafond + " Ar).");
    }
}
