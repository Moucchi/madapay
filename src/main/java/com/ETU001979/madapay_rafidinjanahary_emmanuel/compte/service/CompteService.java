package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Compte;

import java.util.List;
import java.util.UUID;

/** Point d'entrée des autres modules vers les comptes. */
public interface CompteService {

    /** Tous les comptes, ou ceux du numéro donné si {@code telephone} n'est pas nul. */
    List<Compte> lister(String telephone);

    Compte lire(UUID id);

    /** @throws com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.exception.CompteIntrouvableException si le compte n'existe pas */
    void verifierExistence(UUID id);

    /** Débite le compte de façon atomique. @throws com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.exception.SoldeInsuffisantException */
    void debiter(UUID id, long montant);

    /** Crédite le compte de façon atomique (remboursement). */
    void crediter(UUID id, long montant);
}
