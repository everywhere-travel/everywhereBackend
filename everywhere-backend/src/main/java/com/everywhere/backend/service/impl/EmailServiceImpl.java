package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.EmailRequestDTO;
import com.everywhere.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.core.io.ClassPathResource;
import java.time.Year;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Override
    public void sendEmail(EmailRequestDTO emailRequest, List<MultipartFile> files) {
        log.info("Iniciando envío de correo a: {}", emailRequest.getTo());
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setFrom(fromEmail);
            
            if (emailRequest.getTo() != null && !emailRequest.getTo().isEmpty()) {
                helper.setTo(emailRequest.getTo().split("\\s*[,;]\\s*"));
            }
            
            if (emailRequest.getCc() != null && !emailRequest.getCc().isEmpty()) {
                helper.setCc(emailRequest.getCc().split("\\s*[,;]\\s*"));
            }
            
            if (emailRequest.getBcc() != null && !emailRequest.getBcc().isEmpty()) {
                helper.setBcc(emailRequest.getBcc().split("\\s*[,;]\\s*"));
            }

            helper.setSubject(emailRequest.getSubject());
            
            // Usar la plantilla HTML con Thymeleaf
            Context context = new Context();
            String formattedBody = emailRequest.getBody() != null ? emailRequest.getBody().replace("\n", "<br/>") : "";
            context.setVariable("bodyText", formattedBody);
            context.setVariable("year", Year.now().getValue());
            
            String htmlTemplate = templateEngine.process("email-template", context);
            helper.setText(htmlTemplate, true);

            // Agregar el logo como imagen embebida (CID)
            ClassPathResource logoImage = new ClassPathResource("static/images/everyLogo.png");
            if (logoImage.exists()) {
                helper.addInline("logoImage", logoImage, "image/png");
            }

            if (files != null) {
                for (MultipartFile file : files) {
                    if (file != null && !file.isEmpty() && file.getOriginalFilename() != null) {
                        helper.addAttachment(file.getOriginalFilename(), file);
                    }
                }
            }

            mailSender.send(message);
            log.info("Correo enviado exitosamente a: {}", emailRequest.getTo());
        } catch (Exception e) {
            log.error("Error al enviar el correo a {}: {}", emailRequest.getTo(), e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo: " + e.getMessage(), e);
        }
    }
}
