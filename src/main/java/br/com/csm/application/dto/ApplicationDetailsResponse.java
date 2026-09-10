package br.com.csm.application.dto;

import lombok.Builder;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record ApplicationDetailsResponse(
        UUID id,
        String clientId,
        String name,
        String acronym,
        String url,
        String redirectUri,
        Integer status,
        Boolean isPublished,
        AppGovernanceData governanceData,
        AppDateData dateData
) {
    @Builder
    public record AppGovernanceData(
            String objective,
            String notes,
            String requester,
            LocalDate projectStartDate,
            String gitNamespace
    ) {}

    @Builder
    public record AppDateData(
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            OffsetDateTime deactivatedAt
    ) {}
}