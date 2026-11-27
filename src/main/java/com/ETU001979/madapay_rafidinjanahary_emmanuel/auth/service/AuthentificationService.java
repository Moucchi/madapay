package com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.dto.JetonResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthentificationService {

    private final AuthenticationManager authentification;
    private final JetonJwt jetons;
    private final LimiteurConnexion limiteur;

    AuthentificationService(AuthenticationManager authentification, JetonJwt jetons, LimiteurConnexion limiteur) {
        this.authentification = authentification;
        this.jetons = jetons;
        this.limiteur = limiteur;
    }

    public JetonResponse connecter(String telephone, String codePin) {
        var demande = UsernamePasswordAuthenticationToken.unauthenticated(telephone, codePin);
        var resultat = limiteur.authentifier(telephone, () -> authentification.authenticate(demande));

        var identite = (IdentifiantsAuthenticationProvider.Identite) resultat.getPrincipal();
        return jetons.emettre(identite.compteId(), identite.role());
    }
}
