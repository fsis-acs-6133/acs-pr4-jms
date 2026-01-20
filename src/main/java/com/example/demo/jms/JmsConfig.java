package com.example.demo.jms;

import com.example.demo.config.AppJmsProperties;
import com.example.demo.config.AppNotifyProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.ConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
@EnableConfigurationProperties({AppJmsProperties.class, AppNotifyProperties.class})
public class JmsConfig {

    @Bean
    public ObjectMapper jmsObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return om;
    }

    /**
     * Публикуем в Topic (pub/sub)
     */
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jt = new JmsTemplate(connectionFactory);
        jt.setPubSubDomain(true);
        return jt;
    }

    /**
     *    Одна фабрика слушателей, но с ДВУМЯ именами:
     * - "jmsListenerContainerFactory" (дефолт для @JmsListener)
     * - "topicListenerFactory" (чтобы существующие @JmsListener(containerFactory="topicListenerFactory") не падали)
     */
    @Bean(name = {"jmsListenerContainerFactory", "topicListenerFactory"})
    public DefaultJmsListenerContainerFactory listenerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }
}
