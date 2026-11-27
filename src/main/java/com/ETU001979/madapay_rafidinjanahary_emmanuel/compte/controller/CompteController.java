package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.controller;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service.DroitsCompte;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.dto.CompteResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.service.CompteService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ValidationException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageQuery;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation.Telephone;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/comptes")
public class CompteController {

    private final CompteService compteService;
    private final PageService pageService;
    private final DroitsCompte droits;

    public CompteController(CompteService compteService, PageService pageService, DroitsCompte droits) {
        this.compteService = compteService;
        this.pageService = pageService;
        this.droits = droits;
    }

    @GetMapping
    public PageResponse<CompteResponse> lister(@AuthenticationPrincipal Jwt jeton,
                                               @Valid @ModelAttribute PageQuery pagination,
                                               @RequestParam(required = false) String telephone) {
        if (telephone != null && !Telephone.estValide(telephone)) {
            throw ValidationException.surChamp("telephone", Telephone.MESSAGE);
        }
        var comptesVisibles = compteService.lister(telephone).stream()
                .filter(compte -> droits.peutLire(jeton, compte.getId()))
                .toList();
        return pageService.paginer(comptesVisibles, pagination).map(CompteResponse::from);
    }

    @GetMapping("/{compteId}")
    public ResponseEntity<CompteResponse> lire(@AuthenticationPrincipal Jwt jeton, @PathVariable UUID compteId) {
        droits.exigerLecture(jeton, compteId);
        var compte = CompteResponse.from(compteService.lire(compteId));
        return ResponseEntity.ok().eTag(compte.id() + "-" + compte.solde()).body(compte);
    }
}
