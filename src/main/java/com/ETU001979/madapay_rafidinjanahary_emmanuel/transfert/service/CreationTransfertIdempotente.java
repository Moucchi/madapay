package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.dto.TransfertCreationRequest;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.exception.IdempotenceConflitException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.Transfert;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class CreationTransfertIdempotente {

    private final TransfertService transferts;
    private final ConcurrentHashMap<String, Creation> creations = new ConcurrentHashMap<>();

    public CreationTransfertIdempotente(TransfertService transferts) {
        this.transferts = transferts;
    }

    public Resultat creer(String cle, TransfertCreationRequest requete) {
        var corps = Corps.depuis(requete);
        var nouvelleCreation = new AtomicBoolean();
        var creation = creations.computeIfAbsent(cle, ignore -> {
            var transfert = transferts.creer(requete);
            nouvelleCreation.set(true);
            return new Creation(corps, transfert.toBuilder().build());
        });

        if (!creation.corps().equals(corps)) {
            throw new IdempotenceConflitException();
        }
        return new Resultat(creation.transfertInitial().toBuilder().build(), !nouvelleCreation.get());
    }

    public record Resultat(Transfert transfert, boolean rejoue) {
    }

    private record Creation(Corps corps, Transfert transfertInitial) {
    }

    private record Corps(UUID compteSourceId, UUID beneficiaireId, long montant, String motif) {

        private static Corps depuis(TransfertCreationRequest requete) {
            return new Corps(requete.getCompteSourceId(), requete.getBeneficiaireId(),
                    requete.getMontant(), requete.getMotif());
        }
    }
}
