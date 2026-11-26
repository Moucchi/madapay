package com.ETU001979.madapay_rafidinjanahary_emmanuel.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.annotation.JsonDeserialize;

@Data
@JsonDeserialize
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaireCreationDTO {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(
            min = 2,
            max = 80,
            message = "Le nom doit contenir entre 2 et 80 caractères"
    )
    @JsonAlias("nom")
    String nom;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(
            regexp = "^03[2348][0-9]{7}$",
            message = "Le numéro de téléphone est invalide"
    )
    @JsonAlias("telephone")
    String telephone;
}
