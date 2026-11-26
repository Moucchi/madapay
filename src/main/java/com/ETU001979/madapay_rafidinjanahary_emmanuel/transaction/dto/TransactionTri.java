package com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ValidationException;

import java.util.Comparator;

public enum TransactionTri {
    DATE(Comparator.comparing(TransactionResponse::date)),
    DATE_DECROISSANTE(DATE.comparateur.reversed()),
    MONTANT(Comparator.comparingLong(TransactionResponse::montant)),
    MONTANT_DECROISSANT(MONTANT.comparateur.reversed());

    private final Comparator<TransactionResponse> comparateur;

    TransactionTri(Comparator<TransactionResponse> comparateur) {
        this.comparateur = comparateur;
    }

    public static TransactionTri depuis(String valeur) {
        return switch (valeur) {
            case null -> DATE_DECROISSANTE;
            case "-date" -> DATE_DECROISSANTE;
            case "date" -> DATE;
            case "montant" -> MONTANT;
            case "-montant" -> MONTANT_DECROISSANT;
            default -> throw ValidationException.surChamp("tri", "Choisir date, -date, montant ou -montant.");
        };
    }

    public Comparator<TransactionResponse> comparateur() {
        return comparateur.thenComparing(TransactionResponse::id);
    }
}
