package br.com.csm.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "O campo login não pode estar vazio")
    private String login;

    @NotBlank(message = "O campo senha não pode estar vazio")
    private String password;
}
