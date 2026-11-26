package com.ETU001979.madapay_rafidinjanahary_emmanuel.repository;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.Compte;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.CompteDTO;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.exception.CompteNotFoundException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.utils.CompteUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CompteRepositoryImpl implements CompteRepository {
    private final List<Compte> comptes;
    private final CompteUtils compteUtils;

    public CompteRepositoryImpl(@Qualifier("loadComptes") List<Compte> comptes, CompteUtils compteUtils) {
        this.comptes = comptes;
        this.compteUtils = compteUtils;
    }

    @Override
    public CompteDTO getCompteById(String id) {
        Compte compte = comptes.stream()
                .filter(c -> c.getId().toString().equals(id))
                .findFirst()
                .orElseThrow(() -> new CompteNotFoundException("Le compte avec l'ID " + id + " n'existe pas"));

        return compteUtils.toDTO(compte);
    }

    public void verifierExistanceCompte(String compteId) {
        if (comptes.stream().noneMatch(c -> c.getId().toString().equals(compteId))) {
            throw new NoSuchElementException("Aucun compte trouvé avec l'ID : " + compteId);
        }
    }
}
