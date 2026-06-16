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

    @PostMapping("/send")
    @Operation(summary = "Enviar correo", description = "Envía un correo electrónico en texto plano")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailRequestDTO emailRequest) {
        emailService.sendEmail(emailRequest);
        return ResponseEntity.ok().build();
    }
}
