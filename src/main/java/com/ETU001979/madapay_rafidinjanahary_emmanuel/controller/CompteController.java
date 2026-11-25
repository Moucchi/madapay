package com.ETU001979.madapay_rafidinjanahary_emmanuel.controller;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.Compte;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.CompteDTO;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.repository.CompteRepository;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.service.PageService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.utils.CompteUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comptes")
public class CompteController {
    private final List<Compte> comptes;
    private final CompteRepository compteRepository;
    private final PageService pageService;
    private final CompteUtils compteUtils;

    public CompteController(@Qualifier("loadComptes") List<Compte> comptes,
                            CompteRepository compteRepository, PageService pageService, CompteUtils compteUtils) {
        this.comptes = comptes;
        this.compteRepository = compteRepository;
        this.pageService = pageService;
        this.compteUtils = compteUtils;
    }

    @GetMapping
    public PageResponse<CompteDTO> getComptes(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "taille", defaultValue = "20") int taille,
            @RequestParam(value = "telephone", required = false) String telephone
    ) {
        List<CompteDTO> comptesFiltres = comptes.stream()
                .filter(compte -> telephone == null || telephone.equals(compte.getTelephone()))
                .map(compteUtils::toDTO)
                .toList();

        return pageService.getPage(comptesFiltres, page, taille);
    }

    @GetMapping("/{id}")
    public CompteDTO getCompteById(@PathVariable("id") String id) {
        return compteRepository.getCompteById(id);
    }
}
