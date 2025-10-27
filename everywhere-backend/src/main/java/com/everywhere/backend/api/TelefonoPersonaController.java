package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.TelefonoPersonaRequestDTO;
import com.everywhere.backend.model.dto.TelefonoPersonaResponseDTO;
import com.everywhere.backend.service.TelefonoPersonaService;
import com.everywhere.backend.security.RequirePermission;
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
        List<TelefonoPersonaResponseDTO> telefonos = telefonoPersonaService.findByPersonaId(personaId);
        return ResponseEntity.ok(telefonos);
    }

    @GetMapping("/{telefonoId}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<TelefonoPersonaResponseDTO> findById(@PathVariable Integer personaId,
                                                               @PathVariable Integer telefonoId) {
        return telefonoPersonaService.findById(telefonoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<TelefonoPersonaResponseDTO> create(@PathVariable Integer personaId,
                                                             @RequestBody TelefonoPersonaRequestDTO dto) {
        TelefonoPersonaResponseDTO created = telefonoPersonaService.save(dto, personaId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PatchMapping("/{telefonoId}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<TelefonoPersonaResponseDTO> update(@PathVariable Integer personaId,
                                                             @PathVariable Integer telefonoId,
                                                             @RequestBody TelefonoPersonaRequestDTO dto) {
        TelefonoPersonaResponseDTO updated = telefonoPersonaService.update(personaId, dto, telefonoId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{telefonoId}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer personaId,
                                       @PathVariable Integer telefonoId) {
        telefonoPersonaService.deleteById(telefonoId);
        return ResponseEntity.noContent().build();
    }
}
