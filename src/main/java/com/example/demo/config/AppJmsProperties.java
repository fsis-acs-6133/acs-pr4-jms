package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jms")
public class AppJmsProperties {
    private String auditQueue;
    private String notifyQueue;
}
