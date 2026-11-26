package com.ETU001979.madapay_rafidinjanahary_emmanuel.repository;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.Beneficiaire;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.BeneficiaireCreationDTO;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.BeneficiaireModificationDTO;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.exception.DoublonBeneficiaireException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Repository
@Validated
public class BeneficiaireRepository {
    private final Map<String, List<Beneficiaire>> beneficiaires = new java.util.concurrent.ConcurrentHashMap<>();

    private final CompteRepositoryImpl compteRepository;

    public BeneficiaireRepository(CompteRepositoryImpl compteRepository) {
        this.compteRepository = compteRepository;
    }

    public Beneficiaire creerBeneficiaire(@Valid BeneficiaireCreationDTO dto, String compteId) {

        compteRepository.verifierExistanceCompte(compteId);

        List<Beneficiaire> compteBeneficiaires = new ArrayList<>();

        if (beneficiaires.containsKey(compteId)) {
            compteBeneficiaires = beneficiaires.get(compteId);

            boolean doublon = compteBeneficiaires.stream()
                    .anyMatch(b -> b.getTelephone().equals(dto.getTelephone()));

            if (doublon) {
                throw new DoublonBeneficiaireException("Le bénéficiaire avec le numéro de téléphone " + dto.getTelephone() + " existe déjà pour ce compte.");
            }

        }

        UUID beneficiaireId = UUID.randomUUID();

        Beneficiaire nouveauBeneficiaire = Beneficiaire.builder()
                .id(beneficiaireId)
                .compteId(UUID.fromString(compteId))
                .nom(dto.getNom())
                .telephone(dto.getTelephone())
                .creeLe(java.time.Instant.now())
                .build();

        compteBeneficiaires.add(nouveauBeneficiaire);

        beneficiaires.put(compteId, compteBeneficiaires);

        return nouveauBeneficiaire;
    }

    public List<Beneficiaire> getBeneficiaires(String compteId) {

        compteRepository.verifierExistanceCompte(compteId);

        List<Beneficiaire> compteBeneficiaires = beneficiaires.get(compteId);

        if (compteBeneficiaires == null || compteBeneficiaires.isEmpty()) {
            return Collections.emptyList();
        }

        return compteBeneficiaires;
    }

    public Beneficiaire getById(String compteId, String beneficiaireId) {
        compteRepository.verifierExistanceCompte(compteId);

        List<Beneficiaire> compteBeneficiaires = beneficiaires.get(compteId);

        if (compteBeneficiaires == null) {
            return null;
        }

        return compteBeneficiaires.stream()
                .filter(b -> b.getId().toString().equals(beneficiaireId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Aucun bénéficiaire trouvé avec l'ID : " + beneficiaireId));
    }

    public Beneficiaire modifierBenificiaire(String compteId, String id, BeneficiaireModificationDTO dto) {
        List<Beneficiaire> compteBeneficiaires = getBeneficiaires(compteId);

        if (compteBeneficiaires == null) {
            throw new NoSuchElementException("Aucun bénéficiaire trouvé pour le compte avec l'ID : " + compteId);
        }

        Beneficiaire beneficiaireExistant = compteBeneficiaires.stream()
                .filter(b -> b.getId().toString().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Aucun bénéficiaire trouvé avec l'ID : " + id));

        if (!beneficiaireExistant.getTelephone().equals(dto.getTelephone())) {
            boolean doublon = compteBeneficiaires.stream()
                    .anyMatch(b -> b.getTelephone().equals(dto.getTelephone()));

            if (doublon) {
                throw new DoublonBeneficiaireException("Le bénéficiaire avec le numéro de téléphone " + dto.getTelephone() + " existe déjà pour ce compte.");
            }
        }

        if (dto.getNom() != null) {
            if (dto.getNom().isBlank()) {
                throw new IllegalArgumentException("Le nom ne peut pas être vide.");
            }

            beneficiaireExistant.setNom(dto.getNom());
        }

        if (dto.getTelephone() != null) {
            if (dto.getTelephone().isBlank()) {
                throw new IllegalArgumentException("Le telephone ne peut pas être vide.");
            }

            beneficiaireExistant.setTelephone(dto.getTelephone());
        }

        return beneficiaireExistant;
    }

    public void supprimerBeneficiaire(String compteId, String id) {
        List<Beneficiaire> compteBeneficiaires = getBeneficiaires(compteId);

        if (compteBeneficiaires == null || compteBeneficiaires.isEmpty()) {
            throw new NoSuchElementException("Le compte avec l'ID " + compteId + " n'a aucun bénéficiaire.");
        }

        Beneficiaire temp = compteBeneficiaires.stream()
                .filter(b -> b.getId().toString().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Aucun bénéficiaire trouvé avec l'ID : " + id));

        compteBeneficiaires.remove(temp);

        this.beneficiaires.put(compteId, compteBeneficiaires);
    }
}
