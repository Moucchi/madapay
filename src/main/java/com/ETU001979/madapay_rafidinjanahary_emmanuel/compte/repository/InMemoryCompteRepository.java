package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.repository;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Compte;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.repository.seed.CompteSeedLoader;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Comptes en mémoire, chargés depuis le seed au démarrage (ordre du fichier conservé). */
@Repository
public class InMemoryCompteRepository implements CompteRepository {

    private final Map<UUID, Compte> comptes = new LinkedHashMap<>();

    public InMemoryCompteRepository(CompteSeedLoader seedLoader) {
        seedLoader.charger().forEach(c -> comptes.put(c.getId(), c));
    }

    @Override
    public List<Compte> findAll() {
        return List.copyOf(comptes.values());
    }

    @Override
    public Optional<Compte> findById(UUID id) {
        return Optional.ofNullable(comptes.get(id));
    }

    @Override
    public boolean existsById(UUID id) {
        return comptes.containsKey(id);
    }
}
