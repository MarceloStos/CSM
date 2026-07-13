package br.com.csm.dto.application;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CreateResponseDTO {
    private UUID id;
    private String name;
    private String acronym;
    private String clientId;
    private String clientSecret;

}
