package com.ETU001979.madapay_rafidinjanahary_emmanuel.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.exception.PageException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageServiceImpl implements PageService {
    @Override
    public <T> PageResponse<T> getPage(List<T> data, int page, int taille) {
        if (page < 1 || taille < 1 || taille > 100) {
            throw new PageException("page doit être supérieur à 0 et taille doit être comprise entre 1 et 100.");
        }

        int pageMax = Math.max(1, (int) ((data.size() + (long) taille - 1) / taille));

        if (page > pageMax) {
            throw new PageException("page doit être inférieure ou égale à " + pageMax);
        }

        long decalage = (long) (page - 1) * taille;

        List<T> comptesFiltre = data.stream()
                .skip(decalage)
                .limit(taille)
                .toList();

        return new PageResponse<>(page, taille, data.size(), comptesFiltre);
    }
}
