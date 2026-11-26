package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.repository;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Compte;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompteRepository {

    List<Compte> findAll();

    Optional<Compte> findById(UUID id);

    boolean existsById(UUID id);
}
