package com.example.demo.jms;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityChangeMessage {

    private String eventId;
    private Instant occurredAt;

    private ChangeType action;

    private String entityType;
    private Long entityId;

    private Map<String, Object> beforeData;
    private Map<String, Object> afterData;
}
