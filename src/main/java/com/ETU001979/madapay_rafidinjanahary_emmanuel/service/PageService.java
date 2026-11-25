package com.ETU001979.madapay_rafidinjanahary_emmanuel.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.PageResponse;

import java.util.List;

public interface PageService {
    <T> PageResponse<T> getPage(List<T> data, int page, int taille);
}
