package br.com.csm.core.exception;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ApiErrorResponse (
            OffsetDateTime timestamp,
            Integer status,
            String error,
            String message,
            String path
){}