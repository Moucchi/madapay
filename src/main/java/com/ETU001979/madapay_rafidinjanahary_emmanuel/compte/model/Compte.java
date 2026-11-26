package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.exception.SoldeInsuffisantException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/** Compte MadaPay (modèle métier, indépendant du format JSON). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compte {
    private UUID id;
    private String cle;
    private String telephone;
    private String titulaire;
    private String codePin;
    private Role role;
    private long solde;
    private long plafondJournalier;
    private Instant creeLe;

    public boolean peutDebiter(long montant) {
        return montant <= solde;
    }

    /** @throws SoldeInsuffisantException si le solde ne couvre pas le montant */
    public void debiter(long montant) {
        if (!peutDebiter(montant)) {
            throw new SoldeInsuffisantException(solde, montant);
        }
        solde -= montant;
    }

    public void crediter(long montant) {
        solde += montant;
    }
}
