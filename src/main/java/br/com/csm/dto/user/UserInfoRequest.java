package br.com.csm.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class UserInfoRequest {
    @NotBlank(message = "O ID do usuário é obrigatório")
    private UUID id;
}
