package com.example.demo.notify;

import com.example.demo.config.AppNotifyProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AppNotifyProperties props;

    public void send(String subject, String text) {
        log.info("Sending email: from={} to={} subject={}",
                props.getFrom(), props.getTo(), props.getSubjectPrefix() + " " + subject);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(props.getFrom());
        msg.setTo(props.getTo().toArray(new String[0]));
        msg.setSubject(props.getSubjectPrefix() + " " + subject);
        msg.setText(text);

        mailSender.send(msg);
        log.info("Email sent OK");
    }
}
