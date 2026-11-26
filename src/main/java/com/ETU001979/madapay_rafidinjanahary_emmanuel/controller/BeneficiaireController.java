package com.ETU001979.madapay_rafidinjanahary_emmanuel.controller;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.Beneficiaire;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.BeneficiaireCreationDTO;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.BeneficiaireModificationDTO;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.PageRequestDTO;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.repository.BeneficiaireRepository;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.service.PageServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/comptes/{compteId}/beneficiaires")
public class BeneficiaireController {

    private final BeneficiaireRepository beneficiaireRepository;
    private final PageServiceImpl pageService;
    private final int defaultPage;
    private final int defaultTaille;

    public BeneficiaireController(BeneficiaireRepository beneficiaireRepository, PageServiceImpl pageService, @Value("${madapay.pagination.defaut.page}") int defaultPage,
                                  @Value("${madapay.pagination.defaut.taille}") int defaultTaille) {
        this.beneficiaireRepository = beneficiaireRepository;
        this.pageService = pageService;
        this.defaultPage = defaultPage;
        this.defaultTaille = defaultTaille;
    }

    @PostMapping
    public ResponseEntity<?> ajouterBeneficiaire(@Valid @RequestBody BeneficiaireCreationDTO beneficiaire, @PathVariable String compteId, HttpServletRequest req) {
        Beneficiaire result = beneficiaireRepository.creerBeneficiaire(beneficiaire, compteId);
        String locationURI = req.getRequestURI().concat("/".concat(result.getId().toString()));

        return ResponseEntity.created(URI.create(locationURI)).body(result);
    }

    @GetMapping
    public ResponseEntity<?> getBeneficiaires(
            @Valid @ModelAttribute PageRequestDTO pagination,
            @PathVariable String compteId) {
        List<Beneficiaire> result = beneficiaireRepository.getBeneficiaires(compteId);

        return ResponseEntity.ok(pageService.getPage(result, pagination.withDefaults(defaultPage, defaultTaille)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBeneficiairesById(
            @Valid @ModelAttribute PageRequestDTO pagination,
            @PathVariable("compteId") String compteId,
            @PathVariable String id) {

        Beneficiaire result = beneficiaireRepository.getById(compteId, id);

        return ResponseEntity.ok(result);
    }

    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<?> modifierBeneficiaire(@PathVariable("compteId") String compteId,
                                                  @PathVariable("id") String id,
                                                  @Valid @RequestBody BeneficiaireModificationDTO beneficiaire) {
        Beneficiaire result = beneficiaireRepository.modifierBenificiaire(compteId, id, beneficiaire);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerBeneficiaire(@PathVariable("compteId") String compteId,
                                                   @PathVariable("id") String id) {
        beneficiaireRepository.supprimerBeneficiaire(compteId, id);
        return ResponseEntity.noContent().build();
    }

}
