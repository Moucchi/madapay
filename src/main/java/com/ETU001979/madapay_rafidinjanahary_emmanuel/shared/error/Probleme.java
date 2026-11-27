package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Probleme extends ProblemDetail {

    private Code code;
    private List<Erreur> erreurs;

    @Builder
    private static Probleme creer(URI type, String title, int status, String detail,
                                  URI instance, Code code, List<Erreur> erreurs) {
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
