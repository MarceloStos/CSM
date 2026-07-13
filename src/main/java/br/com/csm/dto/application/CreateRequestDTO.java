package br.com.csm.dto.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateRequestDTO {

    @NotBlank(message = "O nome do sistema é obrigatório")
    private String name;
    @NotBlank(message = "O Acrônimo do sistema é obrigatório")
    private String acronym;

    private String url;
    @NotBlank(message = "A URI é necessária para o redirecionamento via OAuth2")
    private String redirectUri;
    private String objective;
    private String notes;

    private String requester;
    private LocalDate projectStartDate;
    private String gitNamespace;

}
