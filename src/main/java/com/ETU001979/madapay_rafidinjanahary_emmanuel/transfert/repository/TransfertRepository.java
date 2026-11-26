package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.repository;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.Transfert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransfertRepository {

    Transfert save(Transfert transfert);

    Optional<Transfert> findById(UUID id);

    List<Transfert> findAllByCompteSource(UUID compteSourceId);
}
