package br.com.csm.audit.message;

import br.com.csm.audit.AuditLog;
import br.com.csm.audit.AuditLogRepository;
import br.com.csm.audit.dto.AuditMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogConsumer {

    private final AuditLogRepository repository;

    @KafkaListener(topics = "csm.audit.events", groupId = "csm-audit-group")
    public void consume(AuditMessage dto) {
        try {
            AuditLog entity = new AuditLog();
            entity.setTimestamp(dto.timestamp());
            entity.setSystemOrigin(dto.systemOrigin());
            entity.setTraceId(dto.traceId());
            entity.setLogLevel(dto.logLevel());
            entity.setEventType(dto.eventType());
            entity.setAction(dto.action());
            entity.setStatus(dto.status());
            // Se o UUID vier como String no DTO, lembre-se de converter: UUID.fromString(dto.UserId())
            entity.setUserId(dto.userId());
            entity.setUsername(dto.username());
            entity.setIpAddress(dto.ipAddress());
            entity.setHttpMethod(dto.httpMethod());
            entity.setEndpoint(dto.endpoint());
            entity.setPayloadRequest(dto.payloadRequest());
            entity.setPayloadResponse(dto.payloadResponse());
            entity.setErrorMessage(dto.errorMessage());
            entity.setStackTrace(dto.stackTrace());

            repository.save(entity);
            log.info("Auditoria salva com sucesso. Trace ID: {}", entity.getTraceId());

        } catch (Exception e) {
            log.error("Falha ao consumir mensagem de auditoria: {}", dto, e);
        }
    }
}