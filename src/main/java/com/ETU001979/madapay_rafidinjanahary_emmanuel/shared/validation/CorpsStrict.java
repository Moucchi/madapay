package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.Erreur;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ValidationException;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class CorpsStrict {

    @JsonIgnore
    private final Set<String> champsInconnus = new LinkedHashSet<>();

    @JsonAnySetter
    public void champInconnu(String nom, Object valeur) {
        champsInconnus.add(nom);
    }

    public void exigerAucunChampInconnu() {
        if (!champsInconnus.isEmpty()) {
            List<Erreur> erreurs = champsInconnus.stream()
                    .map(champ -> new Erreur(champ, "Champ inconnu."))
                    .toList();
            throw new ValidationException("Le corps contient des champs inconnus.", erreurs);
        }
    }
}
