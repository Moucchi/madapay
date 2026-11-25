package com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject;

import java.util.List;

public record PageResponse<T>(
        int page,
        int taille,
        long total,
        List<T> elements
) {
}
