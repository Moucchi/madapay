package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.controller;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.dto.CompteResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.service.CompteService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ValidationException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageQuery;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation.Telephone;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Comptes (contrat, tag « Comptes »). */
@RestController
@RequestMapping("/comptes")
public class CompteController {

    private final CompteService compteService;
    private final PageService pageService;

    public CompteController(CompteService compteService, PageService pageService) {
        this.compteService = compteService;
        this.pageService = pageService;
    }

    /** listerComptes : filtre exact optionnel sur le téléphone. */
    @GetMapping
    public PageResponse<CompteResponse> lister(@Valid @ModelAttribute PageQuery pagination,
                                               @RequestParam(required = false) String telephone) {
        if (telephone != null && !Telephone.estValide(telephone)) {
            throw ValidationException.surChamp("telephone", Telephone.MESSAGE);
        }
        return pageService.paginer(compteService.lister(telephone), pagination)
                .map(CompteResponse::from);
    }

    /** lireCompte. */
    @GetMapping("/{compteId}")
    public CompteResponse lire(@PathVariable UUID compteId) {
        return CompteResponse.from(compteService.lire(compteId));
    }
}
