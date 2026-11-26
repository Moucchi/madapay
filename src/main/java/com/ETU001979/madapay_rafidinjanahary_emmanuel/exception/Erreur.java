package com.ETU001979.madapay_rafidinjanahary_emmanuel.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Erreur {
    String champ;
    String message;
}
