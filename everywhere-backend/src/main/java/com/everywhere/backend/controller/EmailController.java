package com.everywhere.backend.controller;

import com.everywhere.backend.model.dto.EmailRequestDTO;
import com.everywhere.backend.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emails")
@RequiredArgsConstructor
@Tag(name = "Email", description = "API para el envío de correos electrónicos")
public class EmailController {

    private final EmailService emailService;

    @PostMapping(value = "/send", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enviar correo", description = "Envía un correo electrónico con o sin archivos adjuntos")
    public ResponseEntity<Void> sendEmail(
            @RequestPart("email") @Valid EmailRequestDTO emailRequest,
            @RequestPart(value = "files", required = false) java.util.List<org.springframework.web.multipart.MultipartFile> files) {
        emailService.sendEmail(emailRequest, files);
        return ResponseEntity.ok().build();
    }
}
