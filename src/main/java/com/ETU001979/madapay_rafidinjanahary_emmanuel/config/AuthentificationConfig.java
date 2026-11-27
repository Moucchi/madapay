package com.ETU001979.madapay_rafidinjanahary_emmanuel.config;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service.IdentifiantsAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Configuration
public class AuthentificationConfig {

    private static final int TAILLE_CLE_HS256 = 32;

    @Bean
    public AuthenticationManager authenticationManager(IdentifiantsAuthenticationProvider fournisseur) {
        return new ProviderManager(fournisseur);
    }

    @Bean
    public JwtEncoder jwtEncoder(Environment environnement) {
        byte[] octets = cleSignature(environnement.getProperty("JWT_SECRET"));
        var cle = new SecretKeySpec(octets, "HmacSHA256");
        return NimbusJwtEncoder.withSecretKey(cle).algorithm(MacAlgorithm.HS256).build();
    }

    private static byte[] cleSignature(String secret) {
        if (secret == null) {
            var octets = new byte[TAILLE_CLE_HS256];
            new SecureRandom().nextBytes(octets);
            return octets;
        }

        byte[] octets = secret.getBytes(StandardCharsets.UTF_8);
        if (octets.length < TAILLE_CLE_HS256 || secret.isBlank()) {
            throw new IllegalArgumentException("JWT_SECRET doit contenir au moins 32 octets et ne peut pas être vide.");
        }
        return octets;
    }
}
