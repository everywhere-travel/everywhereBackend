package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.chat.ChatMessageRequestDTO;
import com.everywhere.backend.model.dto.chat.ChatMessageResponseDTO;
import com.everywhere.backend.service.AsistenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/asistente")
@RequiredArgsConstructor
public class AsistenteController {

    private final AsistenteService asistenteService;

    @PostMapping("/chat")
    public ResponseEntity<ChatMessageResponseDTO> chat(@Valid @RequestBody ChatMessageRequestDTO request) {
        log.info("Mensaje recibido para el Asistente IA: {}", request.getMessage());
        ChatMessageResponseDTO response = asistenteService.procesarMensaje(request);
        return ResponseEntity.ok(response);
    }
}
