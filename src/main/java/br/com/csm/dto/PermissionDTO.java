package br.com.csm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

public class PermissionDTO {

    @Builder
    public record CreateRequest(
            @NotBlank(message = "O nome da permissão é obrigatório.")
            String name,
            String description,

            @NotNull(message = "O ID da aplicação é obrigatório.")
            UUID applicationId
    ) {}

    @Builder
    public record Response (
            UUID id,
            String name,
            String description,
            UUID applicationId
    ){}

}