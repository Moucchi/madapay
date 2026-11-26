package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.config.MadapayProperties;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageServiceImpl implements PageService {

    private final MadapayProperties.Pagination.Defaut defaut;

    public PageServiceImpl(MadapayProperties properties) {
        this.defaut = properties.pagination().defaut();
    }

    @Override
    public <T> PageResponse<T> paginer(List<T> donnees, PageQuery query) {
        int page = query == null || query.page() == null ? defaut.page() : query.page();
        int taille = query == null || query.taille() == null ? defaut.taille() : query.taille();

        int pageMax = Math.max(1, (int) ((donnees.size() + (long) taille - 1) / taille));
        if (page > pageMax) {
            throw ValidationException.surChamp("page", "La page doit être inférieure ou égale à " + pageMax + ".");
        }

        List<T> elements = donnees.stream()
                .skip((long) (page - 1) * taille)
                .limit(taille)
                .toList();
        return new PageResponse<>(page, taille, donnees.size(), elements);
    }
}
