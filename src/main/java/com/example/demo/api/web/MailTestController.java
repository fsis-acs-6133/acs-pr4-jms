package com.example.demo.web;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailTestController {

    private final JavaMailSender mailSender;

    @PostMapping("/api/test-email")
    public String testEmail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("acs-pr4@localhost");
        msg.setTo("test1@local.test");
        msg.setSubject("TEST MAIL");
        msg.setText("Hello from Spring -> MailHog");
        mailSender.send(msg);
        return "OK";
    }
}
