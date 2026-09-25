package br.com.csm.audit.dto;

import br.com.csm.audit.enums.EventType;
import br.com.csm.audit.enums.LogLevel;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record AuditMessage(
        Instant timestamp,
        String systemOrigin,
        String traceId,
        LogLevel logLevel,
        EventType eventType,
        String action,
        String status,
        UUID userId,
        String username,
        String ipAddress,
        String httpMethod,
        String endpoint,
        String payloadRequest,
        String payloadResponse,
        String errorMessage,
        String stackTrace
) {}