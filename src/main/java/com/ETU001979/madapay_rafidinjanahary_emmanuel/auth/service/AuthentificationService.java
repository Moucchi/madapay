package com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.dto.JetonResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.NonAuthentifieException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthentificationService {

    private final AuthenticationManager authentification;
    private final JetonJwt jetons;

    public AuthentificationService(AuthenticationManager authentification, JetonJwt jetons) {
        this.authentification = authentification;
        this.jetons = jetons;
    }

    public JetonResponse connecter(String telephone, String codePin) {
        var demande = UsernamePasswordAuthenticationToken.unauthenticated(telephone, codePin);
        Authentication resultat;
        try {
            resultat = authentification.authenticate(demande);
        } catch (BadCredentialsException e) {
            throw new NonAuthentifieException();
        }

        var identite = (IdentifiantsAuthenticationProvider.Identite) resultat.getPrincipal();
        return jetons.emettre(identite.compteId(), identite.role());
    }
}
