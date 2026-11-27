package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.pagination;

import java.util.List;

public interface PageService {

    <T> PageResponse<T> paginer(List<T> donnees, PageQuery query);
}
