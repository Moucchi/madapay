package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.dto.BeneficiaireCreationRequest;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.model.Beneficiaire;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BeneficiaireService {

    Beneficiaire creer(UUID compteId, BeneficiaireCreationRequest requete);

    List<Beneficiaire> lister(UUID compteId);

    Beneficiaire lire(UUID compteId, UUID beneficiaireId);

    Beneficiaire modifier(UUID compteId, UUID beneficiaireId, Map<String, Object> patch, String ifMatch);

    void supprimer(UUID compteId, UUID beneficiaireId);
}
