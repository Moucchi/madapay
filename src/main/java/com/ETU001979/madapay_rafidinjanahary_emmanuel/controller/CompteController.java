package com.ETU001979.madapay_rafidinjanahary_emmanuel.controller;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.Compte;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.CompteDTO;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.exception.PageException;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.repository.CompteRepository;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.service.PageService;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.utils.CompteUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.PageRequestDTO;

@RestController
@RequestMapping("/comptes")
public class CompteController {
    private final List<Compte> comptes;
    private final CompteRepository compteRepository;
    private final PageService pageService;
    private final CompteUtils compteUtils;
    private final int defaultPage;
    private final int defaultTaille;

    public CompteController(@Qualifier("loadComptes") List<Compte> comptes,
                            CompteRepository compteRepository, PageService pageService, CompteUtils compteUtils,
                            @Value("${madapay.pagination.defaut.page}") int defaultPage,
                            @Value("${madapay.pagination.defaut.taille}") int defaultTaille) {
        this.comptes = comptes;
        this.compteRepository = compteRepository;
        this.pageService = pageService;
        this.compteUtils = compteUtils;
        this.defaultPage = defaultPage;
        this.defaultTaille = defaultTaille;
    }

    @GetMapping
    public PageResponse<CompteDTO> getComptes(
            @Valid @ModelAttribute PageRequestDTO pagination,
            @RequestParam(value = "telephone", required = false) Optional<String> telephone
    ) {
        if (telephone.filter(numero -> !numero.matches("03[2348][0-9]{7}")).isPresent()) {
            throw new PageException("telephone doit contenir 10 chiffres et commencer par 032, 033, 034 ou 038");
        }

        List<CompteDTO> comptesFiltres = comptes.stream()
                .filter(compte -> telephone.isEmpty()
                        || telephone.get().equals(compte.getTelephone()))
                .map(compteUtils::toDTO)
                .toList();

        return pageService.getPage(
                comptesFiltres,
                pagination.withDefaults(defaultPage, defaultTaille)
        );
    }

    @GetMapping("/{id}")
    public CompteDTO getCompteById(@PathVariable("id") String id) {
        return compteRepository.getCompteById(id);
    }
}
