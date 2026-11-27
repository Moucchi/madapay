package com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.controller;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.dto.IdentifiantsConnexion;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.dto.JetonResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service.AuthentificationService;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthentificationService authentification;

    public AuthController(AuthentificationService authentification) {
        this.authentification = authentification;
    }

    @PostMapping(path = "/token", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JetonResponse> obtenirToken(@RequestBody IdentifiantsConnexion identifiants) {
        identifiants.valider();

        JetonResponse jeton = authentification.connecter(identifiants.telephone(), identifiants.codePin());

        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(jeton);
    }
}
