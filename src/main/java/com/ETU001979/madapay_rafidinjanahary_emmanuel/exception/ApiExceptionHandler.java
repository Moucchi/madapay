package com.ETU001979.madapay_rafidinjanahary_emmanuel.exception;

import java.net.URI;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private final String BASE_TYPE = "https://madapay.example/erreurs/";

    @ExceptionHandler({CompteNotFoundException.class, NoHandlerFoundException.class, NoResourceFoundException.class})
    public ProblemDetail introuvable(Exception exception) {
        return probleme(HttpStatus.NOT_FOUND, "INTROUVABLE", exception.getMessage());
    }

    @ExceptionHandler({PageException.class, MethodArgumentTypeMismatchException.class})
    public ProblemDetail validation(Exception exception) {
        return probleme(HttpStatus.BAD_REQUEST, "VALIDATION", exception.getMessage());
    }

    private ProblemDetail probleme(HttpStatus statut, String code, String detail) {
        ProblemDetail probleme = ProblemDetail.forStatusAndDetail(statut, detail);

        probleme.setType(URI.create(BASE_TYPE + code.toLowerCase(Locale.ROOT)));
        probleme.setProperty("code", code);

        return probleme;
    }
}
