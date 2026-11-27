package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.repository;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.model.Beneficiaire;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryBeneficiaireRepository implements BeneficiaireRepository {

    private final Map<UUID, List<Beneficiaire>> parCompte = new ConcurrentHashMap<>();

    @Override
    public List<Beneficiaire> findAllByCompte(UUID compteId) {
        return List.copyOf(du(compteId));
    }

    @Override
    public Optional<Beneficiaire> findById(UUID compteId, UUID beneficiaireId) {
        return du(compteId).stream()
                .filter(b -> b.getId().equals(beneficiaireId))
                .findFirst();
    }

    @Override
    public boolean existsByTelephone(UUID compteId, String telephone, UUID idExclu) {
        return du(compteId).stream()
                .anyMatch(b -> b.getTelephone().equals(telephone) && !Objects.equals(b.getId(), idExclu));
    }

    @Override
    public Beneficiaire save(Beneficiaire beneficiaire) {
        List<Beneficiaire> liste = parCompte.computeIfAbsent(beneficiaire.getCompteId(), k -> new CopyOnWriteArrayList<>());
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getId().equals(beneficiaire.getId())) {
                liste.set(i, beneficiaire);
                return beneficiaire;
            }
        }
        liste.add(beneficiaire);
        return beneficiaire;
    }

    @Override
    public boolean delete(UUID compteId, UUID beneficiaireId) {
        List<Beneficiaire> liste = parCompte.get(compteId);
        return liste != null && liste.removeIf(b -> b.getId().equals(beneficiaireId));
    }

    private List<Beneficiaire> du(UUID compteId) {
        return parCompte.getOrDefault(compteId, List.of());
    }
}
