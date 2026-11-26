package com.ETU001979.madapay_rafidinjanahary_emmanuel.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.dto.PageRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface PageService {

    <T> PageResponse<T> getPage(
            List<T> data,
            @NotNull @Valid PageRequestDTO pagination
    );
}
