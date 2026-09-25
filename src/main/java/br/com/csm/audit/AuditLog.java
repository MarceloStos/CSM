package br.com.csm.audit;


import br.com.csm.audit.enums.EventType;
import br.com.csm.audit.enums.LogLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;
    @Column(name = "system_origin", nullable = false, length = 100, updatable = false)
    private String systemOrigin;

    @Column(name = "trace_id", length = 64, updatable = false)
    private String traceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_level", nullable = false, length = 20, updatable = false)
    private LogLevel logLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50, updatable = false)
    private EventType eventType;

    @Column(nullable = false, length = 100, updatable = false)
    private String action;

    @Column(length = 20, updatable = false)
    private String status; // SUCCESS, FAILED, WARNING

    @Column(name = "user_id", updatable = false)
    private UUID userId;

    @Column(length = 150, updatable = false)
    private String username;

    @Column(name = "ip_address", length = 45, updatable = false)
    private String ipAddress;

    @Column(name = "http_method", length = 10, updatable = false)
    private String httpMethod;

    @Column(updatable = false)
    private String endpoint;

    // Usando columnDefinition para forçar o PostgreSQL a usar JSONB (visando a performance de busca)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_request", columnDefinition = "jsonb", updatable = false)
    private String payloadRequest;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_response", columnDefinition = "jsonb", updatable = false)
    private String payloadResponse;

    @Column(name = "error_message", columnDefinition = "text", updatable = false)
    private String errorMessage;

    @Column(name = "stack_trace", columnDefinition = "text", updatable = false)
    private String stackTrace;
}
