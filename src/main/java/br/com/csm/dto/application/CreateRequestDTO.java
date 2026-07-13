package br.com.csm.dto.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateRequestDTO (

    @NotBlank(message = "O nome do sistema é obrigatório")
    String name,
    @NotBlank(message = "O Acrônimo do sistema é obrigatório")
    String acronym,
    String url,
    @NotBlank(message = "A URI é necessária para o redirecionamento via OAuth2")
    String redirectUri,
    String objective,
    String notes,

    String requester,
    LocalDate projectStartDate,
    String gitNamespace
    ){}
