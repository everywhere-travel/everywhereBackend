package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.TelefonoPersonaRequestDTO;
import com.everywhere.backend.model.dto.TelefonoPersonaResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.TelefonoPersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personas/{personaId}/telefonos")
@RequiredArgsConstructor
public class TelefonoPersonaController {

    private final TelefonoPersonaService telefonoPersonaService;

    @GetMapping
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<TelefonoPersonaResponseDTO>> findAll(@PathVariable Integer personaId) {
        return ResponseEntity.ok(telefonoPersonaService.findByPersonaId(personaId));
    }

    @GetMapping("/{telefonoId}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<TelefonoPersonaResponseDTO> findById(@PathVariable Integer telefonoId) {
        return telefonoPersonaService.findById(telefonoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<TelefonoPersonaResponseDTO> create(@PathVariable Integer personaId,
                                                             @RequestBody TelefonoPersonaRequestDTO telefonoPersonaRequestDTO) {
        TelefonoPersonaResponseDTO created = telefonoPersonaService.save(telefonoPersonaRequestDTO, personaId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PatchMapping("/{telefonoId}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<TelefonoPersonaResponseDTO> update(@PathVariable Integer personaId,
                                                             @PathVariable Integer telefonoId,
                                                             @RequestBody TelefonoPersonaRequestDTO telefonoPersonaRequestDTO) {
        TelefonoPersonaResponseDTO updated = telefonoPersonaService.update(personaId, telefonoPersonaRequestDTO, telefonoId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{telefonoId}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer telefonoId) {
        telefonoPersonaService.deleteById(telefonoId);
        return ResponseEntity.noContent().build();
    }
}
