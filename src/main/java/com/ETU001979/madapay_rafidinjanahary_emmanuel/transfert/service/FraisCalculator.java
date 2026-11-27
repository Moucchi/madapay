package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.config.MadapayProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FraisCalculator {

    private static final BigDecimal CENT = BigDecimal.valueOf(100);

    private final MadapayProperties.RegleTransfert.Frais regle;

    public FraisCalculator(MadapayProperties properties) {
        this.regle = properties.transfert().frais();
    }

    public long calculer(long montant) {
        if (montant <= regle.seuilSansFrais()) {
            return 0;
        }
        long frais = BigDecimal.valueOf(montant)
                .multiply(regle.pourcentage())
                .divide(CENT, 0, RoundingMode.CEILING)
                .longValueExact();
        return Math.max(frais, regle.min());
    }
}
