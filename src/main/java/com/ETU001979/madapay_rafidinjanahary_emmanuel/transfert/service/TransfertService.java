package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.dto.TransfertCreationRequest;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.Transfert;

import java.util.UUID;

public interface TransfertService {

    /**
     * Crée un transfert EN_ATTENTE et débite le compte source de montant + frais.
     * Contrôles dans l'ordre : comptes/bénéficiaire (404), montant minimal, solde, plafond journalier (422).
     */
    Transfert creer(TransfertCreationRequest requete);

    /** Transfert avec son statut déduit à la lecture. */
    Transfert lire(UUID id);

    /** Annule un transfert encore en attente et restitue le montant débité au compte source. */
    Transfert annuler(UUID id, String raison, String ifMatch);
}
