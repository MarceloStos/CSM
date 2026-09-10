package br.com.csm.user.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserCreateRequest (
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome não pode exceder 100 caracteres")
        String name,

        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "O CPF deve conter exatamente 11 números")
        String cpf,

        @NotBlank(message = "O login é obrigatório")
        @Size(max = 100, message = "O login não pode exceder 100 caracteres")
        String login,

        @Email(message = "Formato de e-mail inválido")
        @Size(max = 150, message = "O e-mail não pode exceder 150 caracteres")
        String email,

        @NotBlank(message = "A senha é obrigatório")
        @Size(min = 8, message = "A senha deve ter no minimo 8 caracteres")
        String password,

        @NotEmpty(message = "O usuário deve ter pelo menos um perfil associado")
        Set<UUID> roleIds
){
}
