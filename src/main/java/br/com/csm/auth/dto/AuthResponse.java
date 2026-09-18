package br.com.csm.auth.dto;


import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Integer expiresIn,
        UserSummary user
) {
    @Builder
    public record UserSummary(
            UUID id,
            String name,
            String login,
            Set<String> roles,
            Set<String> permissions
    ) {
    }
}

