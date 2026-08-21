package br.com.csm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

public class ApplicationDTO {

    @Builder
    public record CreateRequest(

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

    @Builder
    public record CreateResponse(
            UUID id,
            String name,
            String acronym,
            String clientId,
            String clientSecret
    ){}

    @Builder
    public record ApplicationList(
        UUID id,
        String name,
        String Acronym,
        Integer status
    ){}
}
