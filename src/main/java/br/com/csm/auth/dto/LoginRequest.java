package br.com.csm.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
    @NotBlank(message = "O campo login não pode estar vazio")
    String login,
    @NotBlank(message = "O campo senha não pode estar vazio")
    String password
){}

