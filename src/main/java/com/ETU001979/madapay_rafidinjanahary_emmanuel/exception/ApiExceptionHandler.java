package com.ETU001979.madapay_rafidinjanahary_emmanuel.exception;

import java.net.URI;
import java.util.List;
import java.util.Locale;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private final String BASE_TYPE;

    public ApiExceptionHandler(@Value("${madapay.base-type}") String baseType) {
        BASE_TYPE = baseType;
    }

    private Probleme probleme(
            HttpStatus statut, String code, String detail, HttpServletRequest request) {
        return Probleme.builder()
                .type(URI.create(BASE_TYPE + code.toLowerCase(Locale.ROOT)))
                .title(code)
                .status(statut.value())
                .detail(detail)
                .instance(URI.create(request.getRequestURI()))
                .code(code)
                .build();
    }

    private String dernierElementDuChemin(ConstraintViolation<?> violation) {
        String champ = violation.getPropertyPath().toString();
        for (jakarta.validation.Path.Node node : violation.getPropertyPath()) {
            champ = node.getName();
        }
        return champ;
    }

    private String detailErreurs(List<Erreur> erreurs, String detailParDefaut) {
        return erreurs.stream()
                .map(error -> error.getChamp() + ": " + error.getMessage())
                .distinct()
                .reduce((first, next) -> first + "; " + next)
                .orElse(detailParDefaut);
    }

    @ExceptionHandler({
            PageException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    public ProblemDetail validation(Exception exception, HttpServletRequest request) {
        String detail = exception.getMessage();
        List<Erreur> erreurs = List.of();

        if (exception instanceof MethodArgumentNotValidException validationException) {
            erreurs = validationException.getBindingResult().getFieldErrors().stream()
                    .map(error -> new Erreur(error.getField(), error.getDefaultMessage()))
                    .toList();
            detail = detailErreurs(erreurs, exception.getMessage());
        } else if (exception instanceof ConstraintViolationException validationException) {
            erreurs = validationException.getConstraintViolations().stream()
                    .map(violation -> new Erreur(
                            dernierElementDuChemin(violation),
                            violation.getMessage()))
                    .toList();
            detail = detailErreurs(erreurs, exception.getMessage());
        }

        Probleme result = probleme(HttpStatus.BAD_REQUEST, "VALIDATION", detail, request);
        result.setErreurs(erreurs);
        return result;
    }

    @ExceptionHandler({CompteNotFoundException.class, NoHandlerFoundException.class, NoResourceFoundException.class, NoSuchElementException.class})
    public ProblemDetail introuvable(Exception exception, HttpServletRequest request) {
        return probleme(HttpStatus.NOT_FOUND, "INTROUVABLE", exception.getMessage(), request);
    }

    @ExceptionHandler(DoublonBeneficiaireException.class)
    public ProblemDetail doublonBeneficiaire(Exception exception, HttpServletRequest request) {
        return probleme(HttpStatus.CONFLICT, "CONFLIT", exception.getMessage(), request);
    }
}
