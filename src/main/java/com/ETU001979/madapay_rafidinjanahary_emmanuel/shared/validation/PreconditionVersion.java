package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.validation;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.VersionObsoleteException;
import org.springframework.http.ETag;

public final class PreconditionVersion {

    private PreconditionVersion() {
    }

    public static void verifierIfMatch(String ifMatch, String version) {
        if (ifMatch == null || "*".equals(ifMatch.trim())) {
            return;
        }

        ETag courante = new ETag(version, false);
        if (ETag.parse(ifMatch).stream().noneMatch(etag -> courante.compare(etag, true))) {
            throw new VersionObsoleteException();
        }
    }
}
