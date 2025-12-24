package com.example.demo.notify;

import com.example.demo.config.AppNotifyProperties;
import com.example.demo.jms.EntityChangeMessage;
import com.example.demo.jms.EntityChangeMessageParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotifyEmailListener {

    private final AppNotifyProperties props;
    private final EmailService emailService;
    private final EntityChangeMessageParser parser;

    @JmsListener(destination = "${app.jms.notify-queue}")
    public void onMessage(String json) {
        log.info("NOTIFY LISTENER GOT: {}", json);

        if (!props.isEnabled()) {
            log.info("Notify disabled: app.notify.enabled=false");
            return;
        }

        try {
            EntityChangeMessage msg = parser.fromJson(json);

            String subject = "%s %s (id=%s)".formatted(
                    msg.getEntityType(),
                    msg.getAction(),
                    msg.getEntityId()
            );

            String beforeStr = msg.getBeforeData() == null ? "(null)" : msg.getBeforeData().toString();
            String afterStr  = msg.getAfterData()  == null ? "(null)" : msg.getAfterData().toString();

            String body = """
                    Событие: %s
                    Сущность: %s
                    ID: %s
                    Время: %s

                    BEFORE:
                    %s

                    AFTER:
                    %s
                    """.formatted(
                    msg.getAction(),
                    msg.getEntityType(),
                    msg.getEntityId(),
                    msg.getOccurredAt(),
                    beforeStr,
                    afterStr
            );

            emailService.send(subject, body);
            log.info("Email sent for action={} entityType={} id={}",
                    msg.getAction(), msg.getEntityType(), msg.getEntityId());

        } catch (Exception e) {
            log.error("Notify listener failed", e);
            throw e; // чтобы при ошибке JMS не “съел” сообщение молча
        }
    }
}
