package com.ETU001979.madapay_rafidinjanahary_emmanuel.service;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.businessObject.PageResponse;
import com.ETU001979.madapay_rafidinjanahary_emmanuel.exception.PageException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PageServiceImplTests {
    private final PageServiceImpl pageService = new PageServiceImpl();

    @Test
    void returnsRequestedPage() {
        PageResponse<Integer> response = pageService.getPage(List.of(1, 2, 3), 2, 2);

        assertEquals(2, response.page());
        assertEquals(3, response.total());
        assertEquals(List.of(3), response.elements());
    }

    @Test
    void acceptsMaximumPageSize() {
        PageResponse<Integer> response = pageService.getPage(List.of(1), 1, 100);

        assertEquals(100, response.taille());
    }

    @Test
    void rejectsPageSizeAboveMaximum() {
        assertThrows(PageException.class, () -> pageService.getPage(List.of(1), 1, 101));
    }

    @Test
    void rejectsNonPositivePageAndPageSize() {
        assertThrows(PageException.class, () -> pageService.getPage(List.of(1), 0, 1));
        assertThrows(PageException.class, () -> pageService.getPage(List.of(1), 1, 0));
    }
}
