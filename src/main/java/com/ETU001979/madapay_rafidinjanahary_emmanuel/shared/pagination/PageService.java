package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination;

import java.util.List;

/** Découpe une liste en page en appliquant les valeurs par défaut de la configuration. */
public interface PageService {

    <T> PageResponse<T> paginer(List<T> donnees, PageQuery query);
}
