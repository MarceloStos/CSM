package br.com.csm.role.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record RoleUpdateRequest(
        @NotBlank(message = "O nome do perfil é obrigatório")
        @Size(max = 50, message = "O nome não pode exceder 50 caracteres")
        String name,

        @Size(max = 255, message = "A descrição não pode exceder 255 caracteres")
        String description,

        @NotNull(message = "O status é obrigatório")
        @Min(value = 0, message = "Status inválido")
        @Max(value = 1, message = "Status inválido")
        Integer status,

        Set<UUID> permissionIds
) {}