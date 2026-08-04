package br.com.csm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

public class RoleDTO {

    @Builder
    public record CreateRequest (
        @NotBlank(message = "O nome do perfil é obrigatório.")
        String name,
        String description,

        @NotNull(message = "O ID da aplicação é obrigatória.")
        UUID applicationId,
        Set<UUID> permissionIds

    ){}

    @Builder
    public record Response(
            UUID id,
            String name,
            String description,
            UUID applicationId
    ){}
}
