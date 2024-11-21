package com.da.iam.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;


@RequiredArgsConstructor
@Service
public class EmailService {
    private final Logger logger = Logger.getLogger(EmailService.class.getName());

    @Value("${spring.mail.username}")
    private String from;
    @Value("${confirmation.registration.url}")
    private String confirmationRegistrationUrl;

    private final JavaMailSender javaMailSender;
    /**
     * Send email
     *
     * @param to      the email receive this mail
     * @param subject email subject
     * @param body    email body
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while sending mail");
        }
    }

    public void sendConfirmationRegistrationEmail(String to, String token) {
        sendEmail(to, "Confirm registration", "Click here: " + confirmationRegistrationUrl + token);
    }
}
