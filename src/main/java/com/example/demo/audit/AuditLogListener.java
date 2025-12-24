package com.example.demo.audit;

import com.example.demo.jms.EntityChangeMessage;
import com.example.demo.jms.EntityChangeMessageParser;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuditLogListener {

    private final AuditLogRepository repo;
    private final EntityChangeMessageParser parser;

    @Transactional
    @JmsListener(destination = "${app.jms.audit-queue}")
    public void onMessage(String json) {
        EntityChangeMessage msg = parser.fromJson(json);

        AuditLogEntry entry = AuditLogEntry.builder()
                .occurredAt(msg.getOccurredAt())
                .action(msg.getAction().name())
                .entityType(msg.getEntityType())
                .entityId(msg.getEntityId())
                .summary(buildSummary(msg))
                .beforeData(msg.getBeforeData() != null ? msg.getBeforeData().toString() : null)
                .afterData(msg.getAfterData() != null ? msg.getAfterData().toString() : null)
                .build();

        repo.save(entry);
    }

    private String buildSummary(EntityChangeMessage msg) {
        return msg.getAction() + " " + msg.getEntityType() + " id=" + msg.getEntityId();
    }
}
