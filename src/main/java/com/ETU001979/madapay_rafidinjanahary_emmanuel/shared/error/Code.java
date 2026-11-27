package com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error;

public enum Code {
    VALIDATION("Requête invalide", "validation"),
    NON_AUTHENTIFIE("Non authentifié", "non-authentifie"),
    INTERDIT("Accès interdit", "interdit"),
    INTROUVABLE("Ressource introuvable", "introuvable"),
    CONFLIT("Conflit", "conflit"),
    IDEMPOTENCE_CONFLIT("Conflit d'idempotence", "idempotence-conflit"),
    VERSION_OBSOLETE("Précondition échouée", "precondition"),
    SOLDE_INSUFFISANT("Solde insuffisant", "solde-insuffisant"),
    PLAFOND_DEPASSE("Plafond journalier dépassé", "plafond-depasse"),
    MONTANT_TROP_FAIBLE("Montant trop faible", "montant-trop-faible"),
    TRANSFERT_NON_ANNULABLE("Transfert non annulable", "transfert-non-annulable"),
    LIMITE_DEBIT("Trop de requêtes", "limite-debit");

    private final String titre;
    private final String slug;

    Code(String titre, String slug) {
        this.titre = titre;
        this.slug = slug;
    }

    public String titre() {
        return titre;
    }

    public String slug() {
        return slug;
    }
}
