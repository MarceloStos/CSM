package br.com.csm.dto.user;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record UserResponseDTO(
        UUID id,
        String name,
        String cpf,
        String login,
        String email,
        Integer status,
        OffsetDateTime createdAt
) {}
