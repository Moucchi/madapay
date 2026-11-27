package com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DroitsCompte {

    public boolean peutLire(Jwt jeton, UUID compteId) {
        return role(jeton) != Role.CLIENT || estTitulaire(jeton, compteId);
    }

    public void exigerLecture(Jwt jeton, UUID compteId) {
        if (!peutLire(jeton, compteId)) {
            throw new AccessDeniedException("Ce compte ne vous appartient pas.");
        }
    }

    public void exigerTitulaireOuAdmin(Jwt jeton, UUID compteId) {
        var role = role(jeton);
        if (role != Role.ADMIN && (role != Role.CLIENT || !estTitulaire(jeton, compteId))) {
            throw new AccessDeniedException("Cette opération est réservée au titulaire du compte et à l’administrateur.");
        }
    }

    public boolean estAdministrateur(Jwt jeton) {
        return role(jeton) == Role.ADMIN;
    }

    private static Role role(Jwt jeton) {
        return Role.valueOf(jeton.getClaimAsString("role"));
    }

    private static boolean estTitulaire(Jwt jeton, UUID compteId) {
        return compteId.equals(UUID.fromString(jeton.getSubject()));
    }
}
