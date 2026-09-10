package br.com.csm.application.dto;

import lombok.Builder;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ApplicationSummaryResponse(
        UUID id,
        String name,
        String acronym,
        String url,
        Integer status,
        Boolean isPublished,
        OffsetDateTime updatedAt,
        OffsetDateTime createdAt
) {}