package br.com.csm.audit.message;

import br.com.csm.audit.dto.AuditMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogProducer {

    // Injeta o nome do tópico que definimos no application.properties
    @Value("${csm.kafka.topic.audit}")
    private String auditTopic;

    // O KafkaTemplate já é autoconfigurado pelo Spring Boot
    private final KafkaTemplate<String, AuditMessage> kafkaTemplate;

    public void sendLog(AuditMessage message) {
        // Usa um UUID aleatório como chave para garantir que as mensagens sejam distribuídas igualmente entre as partições do Kafka
        String messageKey = message.traceId();

        log.debug("Enviando log de auditoria para o Kafka. Ação: {}", message.action());

        kafkaTemplate.send(auditTopic, messageKey, message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Log enviado com sucesso! Offset: {}", result.getRecordMetadata().offset());
                    } else {
                        log.error("Falha crítica ao enviar log de auditoria para o Kafka: {}", ex.getMessage());
                    }
                });
    }
}