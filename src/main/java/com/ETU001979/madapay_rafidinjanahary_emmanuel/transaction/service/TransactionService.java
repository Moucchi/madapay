package com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.service.CompteService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.config.MadapayProperties;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageQuery;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto.TransactionFiltres;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto.TransactionResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto.TransactionResponse.Type;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto.TransactionTri;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.Transfert;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.TransfertStatut;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.repository.TransfertRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class TransactionService {

    private final CompteService compteService;
    private final TransfertRepository transferts;
    private final PageService pageService;
    private final Clock clock;
    private final Duration fenetreAnnulation;

    public TransactionService(CompteService compteService, TransfertRepository transferts,
                              PageService pageService, Clock clock, MadapayProperties properties) {
        this.compteService = compteService;
        this.transferts = transferts;
        this.pageService = pageService;
        this.clock = clock;
        this.fenetreAnnulation = properties.transfert().fenetreAnnulation();
    }

    public PageResponse<TransactionResponse> lister(UUID compteId, PageQuery pagination,
                                                    TransactionFiltres filtres, TransactionTri tri) {
        compteService.verifierExistence(compteId);
        Instant maintenant = clock.instant();
        List<TransactionResponse> transactions = transferts.findAllByCompteSource(compteId).stream()
                .flatMap(transfert -> transactionsDuTransfert(transfert, maintenant, filtres))
                .sorted(tri.comparateur())
                .toList();
        return pageService.paginer(transactions, pagination);
    }

    private Stream<TransactionResponse> transactionsDuTransfert(Transfert transfert, Instant maintenant,
                                                              TransactionFiltres filtres) {
        TransfertStatut statut = transfert.statutA(maintenant, fenetreAnnulation);
        LocalDate date = LocalDate.ofInstant(transfert.getCreeLe(), clock.getZone());
        if (!filtres.correspond(statut, date)) {
            return Stream.empty();
        }
        TransactionResponse debit = transaction(transfert, Type.DEBIT, transfert.getMontant(), statut);
        if (transfert.getFrais() == 0) {
            return Stream.of(debit);
        }
        return Stream.of(debit, transaction(transfert, Type.FRAIS, transfert.getFrais(), statut));
    }

    private static TransactionResponse transaction(Transfert transfert, Type type, long montant,
                                                   TransfertStatut statut) {
        // L'identifiant reste stable lorsque la transaction est reconstruite à la lecture.
        String cle = transfert.getId() + ":" + type.name();
        UUID id = UUID.nameUUIDFromBytes(cle.getBytes(StandardCharsets.UTF_8));
        return new TransactionResponse(id, transfert.getCompteSourceId(), transfert.getId(),
                type, montant, statut, transfert.getCreeLe());
    }
}
