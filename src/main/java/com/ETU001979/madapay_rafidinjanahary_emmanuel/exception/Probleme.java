package com.ETU001979.madapay_rafidinjanahary_emmanuel.exception;

import java.net.URI;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.ProblemDetail;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Probleme extends ProblemDetail {
    private String code;
    private List<Erreur> erreurs;

    @Builder
    public static Probleme buildProblem(
            URI type,
            String title,
            int status,
            String detail,
            URI instance,
            String code,
            List<Erreur> erreurs) {

        Probleme probleme = new Probleme();
        probleme.setType(type);
        probleme.setTitle(title);
        probleme.setStatus(status);
        probleme.setDetail(detail);
        probleme.setInstance(instance);
        probleme.setCode(code);
        probleme.setErreurs(erreurs);
        return probleme;
    }
}