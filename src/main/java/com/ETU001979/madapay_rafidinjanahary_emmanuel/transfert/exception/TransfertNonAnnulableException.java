package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.exception;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.Code;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ConflitException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.TransfertStatut;

import java.util.UUID;

public class TransfertNonAnnulableException extends ConflitException {

    public TransfertNonAnnulableException(UUID id, TransfertStatut statut) {
        super(Code.TRANSFERT_NON_ANNULABLE, "Le transfert " + id + " ne peut pas être annulé au statut " + statut + ".");
    }
}
