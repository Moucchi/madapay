package com.ETU001979.madapay_rafidinjanahary_emmanuel.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.exception.PageException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageServiceImpl implements PageService {
    @Override
    public <T> PageResponse<T> getPage(List<T> data, int page, int taille) {
        int pageMax = (int) Math.ceil((double) data.size() / taille);
        
        if (pageMax < 1) {
            pageMax = 1;
        }

        int defaultTaille = 20;

        if (page < 1 || taille < 1) {
            throw new PageException("Valeurs incorrectes pour page et taille. Les deux doivent être supérieures à 0.");
        }

        if (page > pageMax) {
            throw new PageException("page doit être inférieure ou égale à " + pageMax);
        }

        int tailleMax = Math.min(defaultTaille, data.size());

        if(tailleMax < defaultTaille){
            if(taille > defaultTaille){
                throw new PageException("Valeur hors limites pour la taille");
            }
        }

        long decalage = (long) (page - 1) * taille;

        List<T> comptesFiltre = data.stream()
                .skip(decalage)
                .limit(taille)
                .toList();

        return new PageResponse<>(page, taille, data.size(), comptesFiltre);
    }
}
