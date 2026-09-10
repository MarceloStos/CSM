package br.com.csm.application.dto;

import lombok.Builder;
import java.util.UUID;

@Builder
public record ApplicationCreateResponse(
        UUID id,
        String name,
        String acronym,
        String clientId,
        String clientSecret // Exibido apenas esta vez!
) {}