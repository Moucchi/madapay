package com.ETU001979.madapay_rafidinjanahary_emmanuel.config;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.shared.error.NonAuthentifieException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecuriteHttpConfig {

    @Bean
    public SecurityFilterChain securiteApi(HttpSecurity http,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver erreurs) throws Exception {
        var connexion = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/auth/token");
        var jetons = new DefaultBearerTokenResolver();
        var roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("role");
        roles.setAuthorityPrefix("ROLE_");
        var authentificationJwt = new JwtAuthenticationConverter();
        authentificationJwt.setJwtGrantedAuthoritiesConverter(roles);
        AuthenticationEntryPoint nonAuthentifie = (requete, reponse, erreur) ->
                erreurs.resolveException(requete, reponse, null,
                        new NonAuthentifieException("Un jeton Bearer valide est requis."));

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(acces -> acces.requestMatchers(connexion).permitAll()
                        .requestMatchers("/transferts/**").hasAnyRole("CLIENT", "ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(nonAuthentifie)
                        .accessDeniedHandler((requete, reponse, erreur) ->
                                erreurs.resolveException(requete, reponse, null, erreur)))
                .oauth2ResourceServer(oauth -> oauth
                        .bearerTokenResolver(requete -> connexion.matches(requete) ? null : jetons.resolve(requete))
                        .authenticationEntryPoint(nonAuthentifie)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(authentificationJwt)))
                .build();
    }
}
