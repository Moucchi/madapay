package com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Role;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.repository.CompteRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;

@Component
public class IdentifiantsAuthenticationProvider implements AuthenticationProvider {

    private final CompteRepository comptes;

    public IdentifiantsAuthenticationProvider(CompteRepository comptes) {
        this.comptes = comptes;
    }

    @Override
    public Authentication authenticate(Authentication demande) {
        if (!(demande.getCredentials() instanceof String codePin)) {
            throw new BadCredentialsException("Identifiants incorrects.");
        }

        var pinSaisi = codePin.getBytes(StandardCharsets.UTF_8);
        var compte = comptes.findByTelephone(demande.getName())
                .filter(c -> MessageDigest.isEqual(c.getCodePin().getBytes(StandardCharsets.UTF_8), pinSaisi))
                .orElseThrow(() -> new BadCredentialsException("Identifiants incorrects."));

        var identite = new Identite(compte.getId(), compte.getRole());
        var droit = new SimpleGrantedAuthority("ROLE_" + identite.role().name());
        return UsernamePasswordAuthenticationToken.authenticated(identite, null, List.of(droit));
    }

    @Override
    public boolean supports(Class<?> type) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(type);
    }

    record Identite(UUID compteId, Role role) {
    }
}
