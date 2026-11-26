package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.service.BeneficiaireService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.exception.SoldeInsuffisantException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Compte;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.service.CompteService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.config.MadapayProperties;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.dto.TransfertCreationRequest;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.exception.MontantTropFaibleException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.exception.PlafondDepasseException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.exception.TransfertIntrouvableException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.Transfert;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.TransfertStatut;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.repository.TransfertRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransfertServiceImpl implements TransfertService {

    private final TransfertRepository repository;
    private final CompteService compteService;
    private final BeneficiaireService beneficiaireService;
    private final FraisCalculator fraisCalculator;
    private final MadapayProperties.RegleTransfert regles;
    private final Clock clock;

    /** Un verrou par compte source : deux transferts du même compte ne se croisent pas (solde, plafond). */
    private final Map<UUID, Object> verrous = new ConcurrentHashMap<>();

    public TransfertServiceImpl(TransfertRepository repository,
                                CompteService compteService,
                                BeneficiaireService beneficiaireService,
                                FraisCalculator fraisCalculator,
                                MadapayProperties properties,
                                Clock clock) {
        this.repository = repository;
        this.compteService = compteService;
        this.beneficiaireService = beneficiaireService;
        this.fraisCalculator = fraisCalculator;
        this.regles = properties.transfert();
        this.clock = clock;
    }

    @Override
    public Transfert creer(TransfertCreationRequest requete) {
        UUID compteId = requete.getCompteSourceId();
        Compte compte = compteService.lire(compteId);                          // 404
        beneficiaireService.lire(compteId, requete.getBeneficiaireId());      // 404 (doit appartenir au compte)

        long montant = requete.getMontant();
        if (montant < regles.montantMin()) {
            throw new MontantTropFaibleException(montant, regles.montantMin()); // 422
        }
        long frais = fraisCalculator.calculer(montant);
        long totalDebite = montant + frais;

        synchronized (verrous.computeIfAbsent(compteId, id -> new Object())) {
            if (!compte.peutDebiter(totalDebite)) {
                throw new SoldeInsuffisantException(compte.getSolde(), totalDebite); // 422
            }
            verifierPlafondJournalier(compte, montant);                         // 422

            compteService.debiter(compteId, totalDebite);
            Instant maintenant = clock.instant();
            return repository.save(Transfert.builder()
                    .id(UUID.randomUUID())
                    .compteSourceId(compteId)
                    .beneficiaireId(requete.getBeneficiaireId())
                    .montant(montant)
                    .frais(frais)
                    .totalDebite(totalDebite)
                    .motif(requete.getMotif())
                    .statut(TransfertStatut.EN_ATTENTE)
                    .creeLe(maintenant)
                    .build());
        }
    }

    @Override
    public Transfert lire(UUID id) {
        Transfert transfert = repository.findById(id).orElseThrow(() -> new TransfertIntrouvableException(id));
        return transfert.toBuilder()
                .statut(transfert.statutA(clock.instant(), regles.fenetreAnnulation()))
                .build();
    }

    /** Plafond : somme des montants (frais exclus) des transferts créés aujourd'hui ; l'atteindre exactement est permis. */
    private void verifierPlafondJournalier(Compte compte, long montant) {
        LocalDate aujourdhui = LocalDate.now(clock);
        long dejaTransfere = repository.findAllByCompteSource(compte.getId()).stream()
                .filter(Transfert::comptePourLePlafond)
                .filter(t -> LocalDate.ofInstant(t.getCreeLe(), clock.getZone()).equals(aujourdhui))
                .mapToLong(Transfert::getMontant)
                .sum();
        if (dejaTransfere + montant > compte.getPlafondJournalier()) {
            throw new PlafondDepasseException(dejaTransfere, montant, compte.getPlafondJournalier());
        }
    }
}
