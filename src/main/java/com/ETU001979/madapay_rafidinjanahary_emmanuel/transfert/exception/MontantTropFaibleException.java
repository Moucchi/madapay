package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.exception;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.Code;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.RegleMetierException;

public class MontantTropFaibleException extends RegleMetierException {

    public MontantTropFaibleException(long montant, long minimum) {
        super(Code.MONTANT_TROP_FAIBLE,
                "Le montant (" + montant + " Ar) est inférieur au minimum autorisé (" + minimum + " Ar).");
    }
}
