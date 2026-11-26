package com.ETU001979.madapay_rafidinjanahary_emmanuel.utils;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.Compte;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.CompteDTO;
import org.springframework.stereotype.Service;

@Service
public class CompteUtils {
    public CompteDTO toDTO(Compte compte){
        return CompteDTO.builder()
                .id(compte.getId().toString())
                .telephone(compte.getTelephone())
                .titulaire(compte.getTitulaire())
                .solde(compte.getSolde())
                .plafondJournalier(compte.getPlafondJournalier())
                .creeLe(compte.getCreeLe())
                .build();
    }
}
