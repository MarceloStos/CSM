package br.com.csm.dto.application;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateResponseDTO (
    UUID id,
    String name,
    String acronym,
    String clientId,
    String clientSecret
){}
