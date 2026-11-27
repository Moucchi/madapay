package com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.controller;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation.IdempotencyKey;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.dto.AnnulationDemande;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.dto.TransfertCreationRequest;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.dto.TransfertResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.model.Transfert;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.transfert.service.TransfertService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/transferts")
public class TransfertController {

    private final TransfertService transfertService;

    public TransfertController(TransfertService transfertService) {
        this.transfertService = transfertService;
    }

    @PostMapping
    public ResponseEntity<TransfertResponse> creer(
            @RequestHeader(value = IdempotencyKey.EN_TETE, required = false) String cleIdempotence,
            @Valid @RequestBody TransfertCreationRequest corps) {
        IdempotencyKey.exiger(cleIdempotence);
        corps.exigerAucunChampInconnu();

        Transfert cree = transfertService.creer(corps);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(cree.getId())
                .toUri();
        return repondre(ResponseEntity.created(location), cree);
    }

    @GetMapping("/{transfertId}")
    public ResponseEntity<TransfertResponse> lire(@PathVariable UUID transfertId) {
        return repondre(ResponseEntity.ok(), transfertService.lire(transfertId));
    }

    @PostMapping("/{transfertId}/annulation")
    public ResponseEntity<TransfertResponse> annuler(
            @PathVariable UUID transfertId,
            @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) String ifMatch,
            @RequestBody(required = false) AnnulationDemande demande) {
        String raison = demande == null ? null : demande.raisonValidee();
        return repondre(ResponseEntity.ok(), transfertService.annuler(transfertId, raison, ifMatch));
    }

    private static ResponseEntity<TransfertResponse> repondre(ResponseEntity.BodyBuilder reponse, Transfert transfert) {
        return reponse.eTag(transfert.version(transfert.getStatut())).body(TransfertResponse.from(transfert));
    }
}
