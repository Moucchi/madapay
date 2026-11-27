package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiaire {

    public static final int NOM_MIN = 2;
    public static final int NOM_MAX = 80;
    public static final String MESSAGE_NOM =
            "Le nom doit contenir entre " + NOM_MIN + " et " + NOM_MAX + " caractères.";

    private UUID id;
    private UUID compteId;
    private String nom;
    private String telephone;
    private Instant creeLe;
    private long revision;

    public String version() {
        return id + "-" + revision;
    }

    public static boolean nomValide(Object valeur) {
        return valeur instanceof String s && !s.isBlank() && s.length() >= NOM_MIN && s.length() <= NOM_MAX;
    }
}
