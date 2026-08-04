package br.com.csm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

public class AuthDTO {

    @Builder
    public record AuthResponse(

            String accessToken,
            String tokenType,
            Integer expiresIn,
            AuthResponse.UserSummary user
    ) {

        @Builder
        public record UserSummary(
                UUID id,
                String name,
                String login
        ) {
        }
    }

    @Builder
    public record LoginRequest (
            @NotBlank(message = "O campo login não pode estar vazio")
            String login,
            @NotBlank(message = "O campo senha não pode estar vazio")
            String password
    ){}

}
