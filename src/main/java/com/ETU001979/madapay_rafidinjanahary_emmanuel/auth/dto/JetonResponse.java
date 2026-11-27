package com.ETU001979.madapay_rafidinjanahary_emmanuel.auth.dto;

import com.ETU001979.madapay_rafidinjanahary_emmanuel.compte.model.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public record JetonResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresIn,
        Role role) {
}
