package com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.repository.seed;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Compte;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Charge le jeu de données initial ; les identifiants sont générés à chaque démarrage. */
@Component
public class CompteSeedLoader {

    private static final String CHEMIN_SEED = "contrat/seed.json";

    private final ObjectMapper mapper;

    public CompteSeedLoader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<Compte> charger() {
        try (InputStream in = new ClassPathResource(CHEMIN_SEED).getInputStream()) {
            SeedFichier seed = mapper.readValue(in, SeedFichier.class);
            Instant maintenant = Instant.now();
            return seed.comptes().stream()
                    .map(c -> Compte.builder()
                            .id(UUID.randomUUID())
                            .cle(c.cle())
                            .telephone(c.telephone())
                            .titulaire(c.titulaire())
                            .codePin(c.codePin())
                            .role(c.role())
                            .solde(c.solde())
                            .plafondJournalier(seed.plafondJournalier())
                            .creeLe(maintenant)
                            .build())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire " + CHEMIN_SEED, e);
        }
    }
}
