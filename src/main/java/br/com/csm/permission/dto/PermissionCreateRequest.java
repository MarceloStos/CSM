package br.com.csm.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PermissionCreateRequest(
        @NotBlank(message = "O nome da permissão é obrigatório")
        @Size(max = 100, message = "O nome não pode exceder 100 caracteres")
        String name,

        @Size(max = 255, message = "A descrição não pode exceder 255 caracteres")
        String description,

        @NotNull(message = "O ID da aplicação é obrigatório")
        UUID applicationId
) {}