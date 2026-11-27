package com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.controller;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.dto.BeneficiaireCreationRequest;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.dto.BeneficiaireResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.beneficiaire.service.BeneficiaireService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageQuery;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/comptes/{compteId}/beneficiaires")
public class BeneficiaireController {

    static final String MERGE_PATCH_JSON = "application/merge-patch+json";

    private final BeneficiaireService beneficiaireService;
    private final PageService pageService;

    public BeneficiaireController(BeneficiaireService beneficiaireService, PageService pageService) {
        this.beneficiaireService = beneficiaireService;
        this.pageService = pageService;
    }

    @PostMapping
    public ResponseEntity<BeneficiaireResponse> creer(@PathVariable UUID compteId,
                                                      @Valid @RequestBody BeneficiaireCreationRequest corps) {
        corps.exigerAucunChampInconnu();
        BeneficiaireResponse cree = BeneficiaireResponse.from(beneficiaireService.creer(compteId, corps));
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(cree.id())
                .toUri();
        return ResponseEntity.created(location).body(cree);
    }

    @GetMapping
    public PageResponse<BeneficiaireResponse> lister(@PathVariable UUID compteId,
                                                     @Valid @ModelAttribute PageQuery pagination) {
        return pageService.paginer(beneficiaireService.lister(compteId), pagination)
                .map(BeneficiaireResponse::from);
    }

    @GetMapping("/{beneficiaireId}")
    public BeneficiaireResponse lire(@PathVariable UUID compteId, @PathVariable UUID beneficiaireId) {
        return BeneficiaireResponse.from(beneficiaireService.lire(compteId, beneficiaireId));
    }

    @PatchMapping(value = "/{beneficiaireId}", consumes = {MERGE_PATCH_JSON, MediaType.APPLICATION_JSON_VALUE})
    public BeneficiaireResponse modifier(@PathVariable UUID compteId,
                                         @PathVariable UUID beneficiaireId,
                                         @RequestBody Map<String, Object> patch) {
        return BeneficiaireResponse.from(beneficiaireService.modifier(compteId, beneficiaireId, patch));
    }

    @DeleteMapping("/{beneficiaireId}")
    public ResponseEntity<Void> supprimer(@PathVariable UUID compteId, @PathVariable UUID beneficiaireId) {
        beneficiaireService.supprimer(compteId, beneficiaireId);
        return ResponseEntity.noContent().build();
    }
}
