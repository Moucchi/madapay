package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ValidationException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation.CorpsStrict;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import tools.jackson.databind.JsonNode;

public class AnnulationDemande extends CorpsStrict {

    @JsonSetter(nulls = Nulls.FAIL)
    private JsonNode raison;

    public String raisonValidee() {
        exigerAucunChampInconnu();
        if (raison == null) {
            return null;
        }
        if (!raison.isString()) {
            throw ValidationException.surChamp("raison", "La raison doit être une chaîne de caractères.");
        }
        String texte = raison.asString();
        if (texte.codePointCount(0, texte.length()) > 140) {
            throw ValidationException.surChamp("raison", "La raison ne doit pas dépasser 140 caractères.");
        }
        return texte;
    }
}
