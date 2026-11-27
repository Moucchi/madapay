package com.ETU001979.madapay_rafidinjanahary_emmanuel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.Duration;

@ConfigurationProperties(prefix = "madapay")
public record MadapayProperties(
        String baseType,
        Pagination pagination,
        RegleTransfert transfert
) {
    public record Pagination(int tailleMax, Defaut defaut) {
        public record Defaut(int page, int taille) {
        }
    }

    public record RegleTransfert(long montantMin, Duration fenetreAnnulation, Frais frais) {

        public record Frais(long seuilSansFrais, long min, BigDecimal pourcentage) {
        }
    }
}
