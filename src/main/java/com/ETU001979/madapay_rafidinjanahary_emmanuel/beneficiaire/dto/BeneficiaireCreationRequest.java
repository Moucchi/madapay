package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.model.Beneficiaire;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation.CorpsStrict;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation.Telephone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BeneficiaireCreationRequest extends CorpsStrict {

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(min = Beneficiaire.NOM_MIN, max = Beneficiaire.NOM_MAX, message = Beneficiaire.MESSAGE_NOM)
    private String nom;

    @NotBlank(message = "Le téléphone est obligatoire.")
    @Pattern(regexp = Telephone.REGEX, message = Telephone.MESSAGE)
    private String telephone;
}
