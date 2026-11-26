package com.ETU001979.madapay_rafidinjanahary_emmanuel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Configuration typée du préfixe {@code madapay} (application.yaml).
 * Une seule source de vérité pour les règles paramétrables.
 */
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

    /**
     * @param montantMin         montant minimal d'un transfert (Ar)
     * @param fenetreAnnulation  durée pendant laquelle un transfert reste EN_ATTENTE (annulable)
     */
    public record RegleTransfert(long montantMin, Duration fenetreAnnulation, Frais frais) {

        /** Frais : 0 jusqu'à {@code seuilSansFrais}, sinon {@code pourcentage} % arrondi à l'Ar supérieur, minimum {@code min}. */
        public record Frais(long seuilSansFrais, long min, BigDecimal pourcentage) {
        }
    }
}
