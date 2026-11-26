package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.dto.BeneficiaireCreationRequest;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.exception.BeneficiaireDoublonException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.exception.BeneficiaireIntrouvableException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.model.Beneficiaire;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.repository.BeneficiaireRepository;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.service.CompteService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.service.BeneficiairePatchValidator.NOM;
import static com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.service.BeneficiairePatchValidator.TELEPHONE;

@Service
public class BeneficiaireServiceImpl implements BeneficiaireService {

    private final BeneficiaireRepository repository;
    private final CompteService compteService;
    private final BeneficiairePatchValidator patchValidator;

    public BeneficiaireServiceImpl(BeneficiaireRepository repository,
                                   CompteService compteService,
                                   BeneficiairePatchValidator patchValidator) {
        this.repository = repository;
        this.compteService = compteService;
        this.patchValidator = patchValidator;
    }

    @Override
    public synchronized Beneficiaire creer(UUID compteId, BeneficiaireCreationRequest requete) {
        compteService.verifierExistence(compteId);
        verifierPasDeDoublon(compteId, requete.getTelephone(), null);

        return repository.save(Beneficiaire.builder()
                .id(UUID.randomUUID())
                .compteId(compteId)
                .nom(requete.getNom())
                .telephone(requete.getTelephone())
                .creeLe(Instant.now())
                .build());
    }

    @Override
    public List<Beneficiaire> lister(UUID compteId) {
        compteService.verifierExistence(compteId);
        return repository.findAllByCompte(compteId);
    }

    @Override
    public Beneficiaire lire(UUID compteId, UUID beneficiaireId) {
        compteService.verifierExistence(compteId);
        return repository.findById(compteId, beneficiaireId)
                .orElseThrow(() -> new BeneficiaireIntrouvableException(beneficiaireId));
    }

    @Override
    public synchronized Beneficiaire modifier(UUID compteId, UUID beneficiaireId, Map<String, Object> patch) {
        Beneficiaire beneficiaire = lire(compteId, beneficiaireId);
        patchValidator.valider(patch);

        if (patch.containsKey(TELEPHONE)) {
            String telephone = (String) patch.get(TELEPHONE);
            verifierPasDeDoublon(compteId, telephone, beneficiaireId);
            beneficiaire.setTelephone(telephone);
        }
        if (patch.containsKey(NOM)) {
            beneficiaire.setNom((String) patch.get(NOM));
        }
        return repository.save(beneficiaire);
    }

    @Override
    public void supprimer(UUID compteId, UUID beneficiaireId) {
        compteService.verifierExistence(compteId);
        if (!repository.delete(compteId, beneficiaireId)) {
            throw new BeneficiaireIntrouvableException(beneficiaireId);
        }
    }

    private void verifierPasDeDoublon(UUID compteId, String telephone, UUID idExclu) {
        if (repository.existsByTelephone(compteId, telephone, idExclu)) {
            throw new BeneficiaireDoublonException(telephone);
        }
    }
}
