package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.repository;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.model.Beneficiaire;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeneficiaireRepository {

    List<Beneficiaire> findAllByCompte(UUID compteId);

    Optional<Beneficiaire> findById(UUID compteId, UUID beneficiaireId);

    /** Vrai si un autre bénéficiaire du compte (hors {@code idExclu}) porte ce numéro. */
    boolean existsByTelephone(UUID compteId, String telephone, UUID idExclu);

    Beneficiaire save(Beneficiaire beneficiaire);

    boolean delete(UUID compteId, UUID beneficiaireId);
}
