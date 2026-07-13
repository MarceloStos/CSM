package br.com.csm.dto.error;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ApiErrorResponseDTO (
    OffsetDateTime timestamp,
    Integer status,
    String error,
    String message,
    String path
){}
