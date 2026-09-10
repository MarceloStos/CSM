package br.com.csm.user.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserUpdateRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome não pode exceder 100 caracteres")
        String name,

        @Email(message = "Formato de e-mail inválido")
        @Size(max = 150, message = "O e-mail não pode exceder 150 caracteres")
        String email,

        @NotNull(message = "O status é obrigatório")
        @Min(value = 0, message = "Status inválido")
        @Max(value = 1, message = "Status inválido")
        Integer status,


        @NotEmpty(message = "O usuário deve ter pelo menos um perfil associado")
        Set<UUID> roleIds
) {}
