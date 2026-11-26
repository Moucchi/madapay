package com.ETU001979.madapay_rafidinjanahary_emmanuel.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.PageRequestDTO;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.exception.PageException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class PageServiceImpl implements PageService {
    @Override
    public <T> PageResponse<T> getPage( List<T> data, PageRequestDTO pagination ) {
        int page = pagination.page();
        int taille = pagination.taille();

        if (page < 1) {
            throw new PageException("page doit être supérieure ou égale à 1");
        }
        if (taille < 1) {
            throw new PageException("taille doit être supérieure ou égale à 1");
        }

        int pageMax = Math.max(1, (int) ((data.size() + (long) taille - 1) / taille));

        if (page > pageMax) {
            throw new PageException( "page doit être inférieure ou égale à " + pageMax );
        }

        long decalage = (long) (page - 1) * taille;

        List<T> elements = data.stream()
                .skip(decalage)
                .limit(taille)
                .toList();

        return new PageResponse<>(
                page, taille, data.size(), elements
        );
    }
}