package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.notify")
public class AppNotifyProperties {
    private boolean enabled = true;
    private String from;
    private List<String> to = new ArrayList<>();
    private String subjectPrefix = "[ACS-PR4]";
}
