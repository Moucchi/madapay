package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.repository;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.Transfert;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTransfertRepository implements TransfertRepository {

    private final Map<UUID, Transfert> transferts = new ConcurrentHashMap<>();

    @Override
    public Transfert save(Transfert transfert) {
        transferts.put(transfert.getId(), transfert);
        return transfert;
    }

    @Override
    public Optional<Transfert> findById(UUID id) {
        return Optional.ofNullable(transferts.get(id));
    }

    @Override
    public List<Transfert> findAllByCompteSource(UUID compteSourceId) {
        return transferts.values().stream()
                .filter(t -> t.getCompteSourceId().equals(compteSourceId))
                .toList();
    }
}
