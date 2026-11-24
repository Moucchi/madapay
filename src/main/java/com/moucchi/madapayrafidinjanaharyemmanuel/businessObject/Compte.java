package com.moucchi.madapayrafidinjanaharyemmanuel.businessObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Compte {
    @JsonProperty("cle")
    String cle;

    @JsonProperty("titulaire")
    String titulaire;

    @JsonProperty("telephone")
    String telephone;

    @JsonProperty("code_pin")
    String codePin;

    @JsonProperty("role")
    Role role;

    @JsonProperty("solde")
    BigDecimal solde;
}
