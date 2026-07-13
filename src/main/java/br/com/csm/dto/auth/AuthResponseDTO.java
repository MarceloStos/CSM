package br.com.csm.dto.auth;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthResponseDTO(

        String accessToken,
        String tokenType,
        Integer expiresIn,
        UserSummary user
) {

    @Builder
    public record UserSummary(
            UUID id,
            String name,
            String login
    ) {
    }
}
