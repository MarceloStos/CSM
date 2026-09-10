package br.com.csm.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ApplicationUpdateRequest(
        @NotBlank(message = "O nome do sistema é obrigatório")
        @Size(max = 200, message = "O nome não pode exceder 200 caracteres")
        String name,

        @NotBlank(message = "O acrônimo do sistema é obrigatório")
        @Size(max = 30, message = "O acrônimo não pode exceder 30 caracteres")
        String acronym,

        @Size(max = 255, message = "A URL não pode exceder 255 caracteres")
        String url,

        @NotNull(message = "O status é obrigatório")
        @Min(value = 0, message = "Status inválido")
        @Max(value = 1, message = "Status inválido")
        Integer status,

        @NotNull(message = "A flag de publicação é obrigatória")
        Boolean isPublished
) {}