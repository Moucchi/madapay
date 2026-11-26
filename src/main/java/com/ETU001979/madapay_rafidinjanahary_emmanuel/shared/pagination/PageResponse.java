package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination;

import java.util.List;
import java.util.function.Function;

/** Page de résultats du contrat : { page, taille, total, elements }. */
public record PageResponse<T>(int page, int taille, long total, List<T> elements) {

    public <R> PageResponse<R> map(Function<? super T, ? extends R> convertisseur) {
        List<R> convertis = elements.stream().<R>map(convertisseur).toList();
        return new PageResponse<>(page, taille, total, convertis);
    }
}
