package br.com.csm.auth.dto;

import lombok.Builder;

@Builder
public record TokenRefreshResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {}