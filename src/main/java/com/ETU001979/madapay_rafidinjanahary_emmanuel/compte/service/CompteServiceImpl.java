package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.exception.CompteIntrouvableException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Compte;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.repository.CompteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompteServiceImpl implements CompteService {

    private final CompteRepository repository;

    public CompteServiceImpl(CompteRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Compte> lister(String telephone) {
        return repository.findAll().stream()
                .filter(c -> telephone == null || telephone.equals(c.getTelephone()))
                .toList();
    }

    @Override
    public Compte lire(UUID id) {
        return repository.findById(id).orElseThrow(() -> new CompteIntrouvableException(id));
    }

    @Override
    public void verifierExistence(UUID id) {
        if (!repository.existsById(id)) {
            throw new CompteIntrouvableException(id);
        }
    }

    @Override
    public void debiter(UUID id, long montant) {
        Compte compte = lire(id);
        synchronized (compte) {
            compte.debiter(montant);
        }
    }

    @Override
    public void crediter(UUID id, long montant) {
        Compte compte = lire(id);
        synchronized (compte) {
            compte.crediter(montant);
        }
    }
}
