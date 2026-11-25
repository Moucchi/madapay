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
            throw new PageException("page doit être supérieure ou égale à 1 et taille doit être comprise entre 1 et 100");
        }

        long decalage = (long) (page - 1) * taille;

        List<T> comptesFiltre = data.stream()
                .skip(decalage)
                .limit(taille)
                .toList();

        return new PageResponse<>(page, taille, data.size(), comptesFiltre);
    }
}
