package com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.Erreur;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ValidationException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation.CorpsStrict;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation.Telephone;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class IdentifiantsConnexion extends CorpsStrict {

    private static final Pattern PIN = Pattern.compile("[0-9]{4}");

    @JsonProperty
    private JsonNode telephone;

    @JsonProperty(value = "code_pin", access = JsonProperty.Access.WRITE_ONLY)
    private JsonNode codePin;

    public void valider() {
        exigerAucunChampInconnu();

        List<Erreur> erreurs = new ArrayList<>();

        if (!Telephone.estValide(telephone())) {
            erreurs.add(new Erreur("telephone", Telephone.MESSAGE));
        }

        String pin = codePin();

        if (pin == null || !PIN.matcher(pin).matches()) {
            erreurs.add(new Erreur("code_pin", "Le code PIN doit être une chaîne de quatre chiffres."));
        }

        if (!erreurs.isEmpty()) {
            throw new ValidationException("Les identifiants sont invalides.", erreurs);
        }
    }

    public String telephone() {
        return chaineJson(telephone);
    }

    public String codePin() {
        return chaineJson(codePin);
    }

    private static String chaineJson(JsonNode valeur) {
        return valeur != null && valeur.isString() ? valeur.stringValue() : null;
    }
}
