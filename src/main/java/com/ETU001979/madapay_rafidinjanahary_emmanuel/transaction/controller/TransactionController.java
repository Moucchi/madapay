package com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.controller;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.service.DroitsCompte;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageQuery;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto.TransactionFiltres;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto.TransactionResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto.TransactionTri;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.service.TransactionService;
import jakarta.validation.Valid;
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
@RequestMapping("/comptes/{compteId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final DroitsCompte droits;

    public TransactionController(TransactionService transactionService, DroitsCompte droits) {
        this.transactionService = transactionService;
        this.droits = droits;
    }

    @GetMapping
    public PageResponse<TransactionResponse> lister(
            @AuthenticationPrincipal Jwt jeton,
            @PathVariable UUID compteId,
            @Valid @ModelAttribute PageQuery pagination,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String du,
            @RequestParam(required = false) String au,
            @RequestParam(required = false) String tri) {
        droits.exigerLecture(jeton, compteId);
        return transactionService.lister(compteId, pagination, TransactionFiltres.depuis(statut, du, au),
                TransactionTri.depuis(tri));
    }
}
