package br.com.csm.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String cpf;
    @NotBlank
    private String login;

}
