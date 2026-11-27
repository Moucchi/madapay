package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.model.Beneficiaire;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.Erreur;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ValidationException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation.Telephone;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class BeneficiairePatchValidator {

    static final String NOM = "nom";
    static final String TELEPHONE = "telephone";
    private static final Set<String> CHAMPS_MODIFIABLES = Set.of(NOM, TELEPHONE);

    public void valider(Map<String, Object> patch) {
        if (patch == null || patch.isEmpty()) {
            throw ValidationException.surChamp("corps", "Au moins un champ à modifier est attendu (nom ou telephone).");
        }

        List<Erreur> erreurs = new ArrayList<>();
        patch.keySet().stream()
                .filter(champ -> !CHAMPS_MODIFIABLES.contains(champ))
                .forEach(champ -> erreurs.add(new Erreur(champ, "Champ inconnu.")));
        if (patch.containsKey(NOM) && !Beneficiaire.nomValide(patch.get(NOM))) {
            erreurs.add(new Erreur(NOM, Beneficiaire.MESSAGE_NOM));
        }
        if (patch.containsKey(TELEPHONE) && !Telephone.estValide(patch.get(TELEPHONE))) {
            erreurs.add(new Erreur(TELEPHONE, Telephone.MESSAGE));
        }

        if (!erreurs.isEmpty()) {
            throw new ValidationException("Un ou plusieurs champs sont invalides.", erreurs);
        }
    }
}
