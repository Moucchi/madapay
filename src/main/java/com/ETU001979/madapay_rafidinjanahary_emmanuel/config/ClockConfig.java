package com.ETU001979.madapay_rafidinjanahary_emmanuel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** Horloge injectable : rend testables les règles dépendant du temps (plafond du jour, fenêtre d'annulation). */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
