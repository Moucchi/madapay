package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Compte;

import java.util.List;
import java.util.UUID;

public interface CompteService {

    List<Compte> lister(String telephone);

    Compte lire(UUID id);

    void verifierExistence(UUID id);

    void debiter(UUID id, long montant);

    void crediter(UUID id, long montant);
}
