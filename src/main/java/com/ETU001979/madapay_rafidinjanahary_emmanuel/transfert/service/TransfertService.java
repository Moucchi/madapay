package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.dto.TransfertCreationRequest;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.Transfert;

import java.util.UUID;

public interface TransfertService {

    Transfert creer(TransfertCreationRequest requete);

    Transfert lire(UUID id);

    Transfert annuler(UUID id, String raison, String ifMatch);
}
