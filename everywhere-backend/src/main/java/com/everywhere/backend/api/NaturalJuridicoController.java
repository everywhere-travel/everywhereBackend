package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.NaturalJuridicoRequestDTO;
import com.everywhere.backend.model.dto.NaturalJuridicoResponseDTO;
import com.everywhere.backend.model.dto.NaturalJuridicoPatchDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.NaturalJuridicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/natural-juridico")
@RequiredArgsConstructor
public class NaturalJuridicoController {

    private final NaturalJuridicoService naturalJuridicoService;

    @GetMapping
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<NaturalJuridicoResponseDTO>> getAllRelaciones() {
        List<NaturalJuridicoResponseDTO> relaciones = naturalJuridicoService.findAll();
        return ResponseEntity.ok(relaciones);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<NaturalJuridicoResponseDTO> getRelacionById(@PathVariable Integer id) {
        NaturalJuridicoResponseDTO relacion = naturalJuridicoService.findById(id);
        return ResponseEntity.ok(relacion);
    }

    @GetMapping("/persona-natural/{personaNaturalId}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<NaturalJuridicoResponseDTO>> getRelacionesByPersonaNatural(
            @PathVariable Integer personaNaturalId) {
        List<NaturalJuridicoResponseDTO> relaciones = naturalJuridicoService.findByPersonaNaturalId(personaNaturalId);
        return ResponseEntity.ok(relaciones);
    }

    @GetMapping("/persona-juridica/{personaJuridicaId}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<NaturalJuridicoResponseDTO>> getRelacionesByPersonaJuridica(
            @PathVariable Integer personaJuridicaId) {
        List<NaturalJuridicoResponseDTO> relaciones = naturalJuridicoService.findByPersonaJuridicaId(personaJuridicaId);
        return ResponseEntity.ok(relaciones);
    }

    @PostMapping
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<List<NaturalJuridicoResponseDTO>> crearRelaciones(
            @Valid @RequestBody NaturalJuridicoRequestDTO naturalJuridicoRequestDTO) {
        List<NaturalJuridicoResponseDTO> relacionesCreadas = naturalJuridicoService.crearRelaciones(naturalJuridicoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(relacionesCreadas);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deleteRelacion(@PathVariable Integer id) {
        naturalJuridicoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/persona-natural/{personaNaturalId}/persona-juridica/{personaJuridicaId}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deleteRelacionByPersonas(
            @PathVariable Integer personaNaturalId,
            @PathVariable Integer personaJuridicaId) {
        naturalJuridicoService.deleteByPersonas(personaNaturalId, personaJuridicaId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/persona-natural/{personaNaturalId}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<List<NaturalJuridicoResponseDTO>> patchRelacionesPersonaNatural(
            @PathVariable Integer personaNaturalId, @RequestBody NaturalJuridicoPatchDTO naturalJuridicoPatchDTO) {
        List<NaturalJuridicoResponseDTO> relacionesActualizadas = naturalJuridicoService.patchRelacionesPersonaNatural(personaNaturalId, naturalJuridicoPatchDTO);
        return ResponseEntity.ok(relacionesActualizadas);
    }
}