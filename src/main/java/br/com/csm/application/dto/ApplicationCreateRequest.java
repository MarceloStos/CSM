package br.com.csm.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ApplicationCreateRequest(
        @NotBlank(message = "O nome do sistema é obrigatório")
        @Size(max = 200, message = "O nome não pode exceder 200 caracteres")
        String name,

        @NotBlank(message = "O acrônimo do sistema é obrigatório")
        @Size(max = 30, message = "O acrônimo não pode exceder 30 caracteres")
        String acronym,

        @Size(max = 255, message = "A URL não pode exceder 255 caracteres")
        String url,

        @NotBlank(message = "A URI é necessária para o redirecionamento via OAuth2")
        @Size(max = 255, message = "A URI de redirecionamento não pode exceder 255 caracteres")
        String redirectUri,

        String objective,
        String notes,

        @Size(max = 255, message = "O solicitante não pode exceder 255 caracteres")
        String requester,

        LocalDate projectStartDate,

        @Size(max = 200, message = "O namespace do Git não pode exceder 200 caracteres")
        String gitNamespace
) {}