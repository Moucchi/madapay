package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.config.MadapayProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tools.jackson.databind.DatabindException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestControllerAdvice
public class ApiExceptionHandler {

    private final String baseType;

    public ApiExceptionHandler(MadapayProperties properties) {
        this.baseType = properties.baseType();
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail champsInvalides(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<Erreur> erreurs = e.getBindingResult().getFieldErrors().stream()
                .map(err -> new Erreur(err.getField(), err.getDefaultMessage()))
                .toList();
        return probleme(HttpStatus.BAD_REQUEST, Code.VALIDATION, resume(erreurs), req, erreurs);
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail validation(ValidationException e, HttpServletRequest req) {
        return probleme(HttpStatus.BAD_REQUEST, Code.VALIDATION, e.getMessage(), req, e.getErreurs());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail corpsIllisible(HttpMessageNotReadableException e, HttpServletRequest req) {
        List<Erreur> erreurs = List.of(champFautif(e)
                .map(champ -> new Erreur(champ, "Valeur invalide pour le type attendu."))
                .orElseGet(() -> new Erreur("corps", "Le corps est absent ou n'est pas un JSON valide.")));
        return probleme(HttpStatus.BAD_REQUEST, Code.VALIDATION, resume(erreurs), req, erreurs);
    }

    private static Optional<String> champFautif(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof DatabindException de && !de.getPath().isEmpty()) {
                return Optional.ofNullable(de.getPath().getLast().getPropertyName());
            }
        }
        return Optional.empty();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail typeInvalide(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        if (e.getParameter().hasParameterAnnotation(PathVariable.class)) {
            return probleme(HttpStatus.NOT_FOUND, Code.INTROUVABLE,
                    "Aucune ressource ne porte l'identifiant " + e.getValue() + ".", req, null);
        }
        List<Erreur> erreurs = List.of(new Erreur(e.getName(), "Valeur invalide : " + e.getValue()));
        return probleme(HttpStatus.BAD_REQUEST, Code.VALIDATION, resume(erreurs), req, erreurs);
    }


    @ExceptionHandler(RessourceIntrouvableException.class)
    public ProblemDetail introuvable(RessourceIntrouvableException e, HttpServletRequest req) {
        return probleme(HttpStatus.NOT_FOUND, Code.INTROUVABLE, e.getMessage(), req, null);
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ProblemDetail routeInconnue(Exception e, HttpServletRequest req) {
        return probleme(HttpStatus.NOT_FOUND, Code.INTROUVABLE,
                "La route " + req.getMethod() + " " + req.getRequestURI() + " n'existe pas.", req, null);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail methodeNonAutorisee(HttpRequestMethodNotSupportedException e, HttpServletRequest req) {
        return probleme(HttpStatus.METHOD_NOT_ALLOWED, null, e.getMessage(), req, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail typeNonSupporte(HttpMediaTypeNotSupportedException e, HttpServletRequest req) {
        return probleme(HttpStatus.UNSUPPORTED_MEDIA_TYPE, null, e.getMessage(), req, null);
    }


    @ExceptionHandler(ConflitException.class)
    public ProblemDetail conflit(ConflitException e, HttpServletRequest req) {
        return probleme(HttpStatus.CONFLICT, e.getCode(), e.getMessage(), req, null);
    }

    @ExceptionHandler(VersionObsoleteException.class)
    public ProblemDetail versionObsolete(VersionObsoleteException e, HttpServletRequest req) {
        return probleme(HttpStatus.PRECONDITION_FAILED, Code.VERSION_OBSOLETE, e.getMessage(), req, null);
    }

    @ExceptionHandler(RegleMetierException.class)
    public ProblemDetail regleMetier(RegleMetierException e, HttpServletRequest req) {
        return probleme(HttpStatus.valueOf(422), e.getCode(), e.getMessage(), req, null);
    }


    private Probleme probleme(HttpStatus statut, Code code, String detail,
                              HttpServletRequest req, List<Erreur> erreurs) {
        return Probleme.builder()
                .type(URI.create(baseType + (code != null ? code.slug() : String.valueOf(statut.value()))))
                .title(code != null ? code.titre() : statut.getReasonPhrase())
                .status(statut.value())
                .detail(detail)
                .instance(URI.create(req.getRequestURI()))
                .code(code)
                .erreurs(erreurs == null || erreurs.isEmpty() ? null : erreurs)
                .build();
    }

    private static String resume(List<Erreur> erreurs) {
        return erreurs.stream()
                .map(e -> e.champ() + " : " + e.message())
                .distinct()
                .reduce((a, b) -> a + " ; " + b)
                .orElse("Un ou plusieurs champs sont invalides.");
    }
}
