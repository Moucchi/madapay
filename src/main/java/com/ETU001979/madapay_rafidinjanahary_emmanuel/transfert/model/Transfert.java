package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Transfert {
    private UUID id;
    private UUID compteSourceId;
    private UUID beneficiaireId;
    private long montant;
    private long frais;
    private long totalDebite;
    private String motif;
    private TransfertStatut statut;
    private Instant creeLe;
    private Instant misAJourLe;
    private String raisonAnnulation;

    /** Le statut courant identifie la version, y compris après expiration du délai d'annulation. */
    public String version(TransfertStatut statutCourant) {
        return id + "-" + statutCourant;
    }

    /**
     * Statut déduit à la lecture (contrat) : un transfert EN_ATTENTE devient REUSSIE
     * une fois la fenêtre d'annulation écoulée.
     */
    public TransfertStatut statutA(Instant maintenant, Duration fenetreAnnulation) {
        if (statut == TransfertStatut.EN_ATTENTE && !maintenant.isBefore(creeLe.plus(fenetreAnnulation))) {
            return TransfertStatut.REUSSIE;
        }
        return statut;
    }

    /** Vrai si le montant compte dans le plafond journalier (un transfert annulé est remboursé). */
    public boolean comptePourLePlafond() {
        return statut != TransfertStatut.ANNULEE && statut != TransfertStatut.ECHOUEE;
    }
}
