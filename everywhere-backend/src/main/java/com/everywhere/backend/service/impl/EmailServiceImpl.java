package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.EmailRequestDTO;
import com.everywhere.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Override
    public void sendEmail(EmailRequestDTO emailRequest) {
        log.info("Iniciando envío de correo a: {}", emailRequest.getTo());
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(emailRequest.getTo());
            message.setSubject(emailRequest.getSubject());
            message.setText(emailRequest.getBody());

            mailSender.send(message);
            log.info("Correo enviado exitosamente a: {}", emailRequest.getTo());
        } catch (Exception e) {
            log.error("Error al enviar el correo a {}: {}", emailRequest.getTo(), e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo: " + e.getMessage(), e);
        }
    }
}
