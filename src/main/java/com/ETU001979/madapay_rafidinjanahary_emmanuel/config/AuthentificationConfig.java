package com.ETU001979.madapay_rafidinjanahary_emmanuel.config;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service.IdentifiantsAuthenticationProvider;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.util.UUID;

@Configuration
public class AuthentificationConfig {

    private static final int TAILLE_CLE_HS256 = 32;

    @Bean
    public AuthenticationManager authenticationManager(IdentifiantsAuthenticationProvider fournisseur) {
        return new ProviderManager(fournisseur);
    }

    @Bean
    public SecretKey cleJwt(Environment environnement) {
        return new SecretKeySpec(cleSignature(environnement.getProperty("JWT_SECRET")), "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey cle) {
        return NimbusJwtEncoder.withSecretKey(cle).algorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey cle, Clock horloge) {
        var decodeur = NimbusJwtDecoder.withSecretKey(cle).macAlgorithm(MacAlgorithm.HS256).build();
        var dates = new JwtTimestampValidator(Duration.ZERO);
        dates.setClock(horloge);
        decodeur.setJwtValidator(jeton -> {
            try {
                UUID.fromString(jeton.getSubject());
                Role.valueOf(jeton.getClaimAsString("role"));
                if (jeton.getExpiresAt() != null && horloge.instant().isBefore(jeton.getExpiresAt())) {
                    return dates.validate(jeton);
                }
            } catch (IllegalArgumentException | NullPointerException e) {
                return jetonInvalide();
            }
            return jetonInvalide();
        });
        return decodeur;
    }

    private static OAuth2TokenValidatorResult jetonInvalide() {
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
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
