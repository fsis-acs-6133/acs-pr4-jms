package com.example.demo.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityChangeMessageParser {

    private final ObjectMapper jmsObjectMapper;

    public String toJson(EntityChangeMessage msg) {
        try {
            return jmsObjectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize JMS event", e);
        }
    }

    public EntityChangeMessage fromJson(String json) {
        try {
            return jmsObjectMapper.readValue(json, EntityChangeMessage.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JMS message JSON", e);
        }
    }
}
