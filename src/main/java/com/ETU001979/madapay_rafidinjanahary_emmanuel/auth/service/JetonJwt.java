package com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.dto.JetonResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Role;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.util.UUID;

@Component
public class JetonJwt {

    private static final Duration VALIDITE = Duration.ofHours(1);
    private static final JwsHeader ENTETE = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();

    private final JwtEncoder encodeur;
    private final Clock horloge;

    public JetonJwt(JwtEncoder encodeur, Clock horloge) {
        this.encodeur = encodeur;
        this.horloge = horloge;
    }

    public JetonResponse emettre(UUID compteId, Role role) {
        var maintenant = horloge.instant();
        var claims = JwtClaimsSet.builder()
                .subject(compteId.toString())
                .claim("role", role.name())
                .issuedAt(maintenant)
                .expiresAt(maintenant.plus(VALIDITE))
                .build();
        var jeton = encodeur.encode(JwtEncoderParameters.from(ENTETE, claims));

        return JetonResponse.builder()
                .accessToken(jeton.getTokenValue())
                .tokenType("Bearer")
                .expiresIn(VALIDITE.toSeconds())
                .role(role)
                .build();
    }
}
