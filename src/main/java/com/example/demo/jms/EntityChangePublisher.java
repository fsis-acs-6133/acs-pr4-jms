package com.example.demo.jms;

import com.example.demo.config.AppJmsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
public class EntityChangePublisher {

    private final JmsTemplate jmsTemplate;
    private final AppJmsProperties props;
    private final EntityChangeMessageParser parser;

    public void publishAfterCommit(EntityChangeMessage event) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(event);
                }
            });
        } else {
            send(event);
        }
    }

    private void send(EntityChangeMessage event) {
        String json = parser.toJson(event);

        // ДВА очереди => и аудит, и уведомления получат одинаковое событие
        jmsTemplate.convertAndSend(props.getAuditQueue(), json);
        jmsTemplate.convertAndSend(props.getNotifyQueue(), json);
    }
}
