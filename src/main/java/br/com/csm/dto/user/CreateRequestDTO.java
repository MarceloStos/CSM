package br.com.csm.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateRequestDTO (
    @NotBlank(message = "O nome é obrigatótrio")
    String name,
    @NotBlank(message = "O CPF é obrigatório")
    String cpf,
    @NotBlank(message = "O login é obrigatótrio")
    String login,
    @Email(message = "Formato de e-mail inválido")
    String email,
    @NotBlank(message = "A senha é obrigatótrio")
    @Size(min = 8, message = "A senha deve ter no minimo 8 caracteres")
    String password
    ){
}
