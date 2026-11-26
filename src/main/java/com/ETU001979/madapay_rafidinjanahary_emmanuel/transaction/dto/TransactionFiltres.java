package com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ValidationException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.TransfertStatut;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public record TransactionFiltres(TransfertStatut statut, LocalDate du, LocalDate au) {

    public static TransactionFiltres depuis(String statut, String du, String au) {
        TransfertStatut statutFiltre = lireStatut(statut);
        LocalDate debut = lireDate("du", du);
        LocalDate fin = lireDate("au", au);
        if (debut != null && fin != null && fin.isBefore(debut)) {
            throw ValidationException.surChamp("au", "La date de fin doit être postérieure ou égale à la date de début.");
        }
        return new TransactionFiltres(statutFiltre, debut, fin);
    }

    public boolean correspond(TransfertStatut statutTransfert, LocalDate date) {
        return (statut == null || statut == statutTransfert)
                && (du == null || !date.isBefore(du))
                && (au == null || !date.isAfter(au));
    }

    private static TransfertStatut lireStatut(String valeur) {
        if (valeur == null) {
            return null;
        }
        try {
            return TransfertStatut.valueOf(valeur);
        } catch (IllegalArgumentException e) {
            throw ValidationException.surChamp("statut", "Statut de transfert inconnu.");
        }
    }

    private static LocalDate lireDate(String champ, String valeur) {
        if (valeur == null) {
            return null;
        }
        String message = "Une date valide au format AAAA-MM-JJ est attendue.";
        if (!valeur.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            throw ValidationException.surChamp(champ, message);
        }
        try {
            return LocalDate.parse(valeur);
        } catch (DateTimeParseException e) {
            throw ValidationException.surChamp(champ, message);
        }
    }
}
