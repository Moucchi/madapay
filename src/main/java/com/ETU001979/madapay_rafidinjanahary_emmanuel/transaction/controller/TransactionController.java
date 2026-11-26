package com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.controller;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageQuery;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto.TransactionFiltres;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto.TransactionResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.dto.TransactionTri;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transaction.service.TransactionService;
import jakarta.validation.Valid;
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

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public PageResponse<TransactionResponse> lister(
            @PathVariable UUID compteId,
            @Valid @ModelAttribute PageQuery pagination,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String du,
            @RequestParam(required = false) String au,
            @RequestParam(required = false) String tri) {
        return transactionService.lister(compteId, pagination, TransactionFiltres.depuis(statut, du, au),
                TransactionTri.depuis(tri));
    }
}
