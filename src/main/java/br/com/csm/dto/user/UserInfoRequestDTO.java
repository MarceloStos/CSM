package br.com.csm.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserInfoRequestDTO (
    @NotBlank(message = "O ID do usuário é obrigatório")
    UUID id
){
}
