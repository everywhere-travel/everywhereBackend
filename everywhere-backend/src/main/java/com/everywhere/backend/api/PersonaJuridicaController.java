package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.PersonaJuridicaRequestDTO;
import com.everywhere.backend.model.dto.PersonaJuridicaResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.PersonaJuridicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personas-juridicas")
@RequiredArgsConstructor
public class PersonaJuridicaController {

    private final PersonaJuridicaService personaJuridicaService;

    @GetMapping
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaJuridicaResponseDTO>> getAllPersonasJuridicas() {
        List<PersonaJuridicaResponseDTO> personas = personaJuridicaService.findAll();
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/ruc")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaJuridicaResponseDTO>> getPersonasJuridicasByRUC(@RequestParam String ruc) {
        List<PersonaJuridicaResponseDTO> personas = personaJuridicaService.findByRuc(ruc.trim());
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/razSocial")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaJuridicaResponseDTO>> getPersonasJuridicasByRazSocial(@RequestParam String razonSocial) {
        List<PersonaJuridicaResponseDTO> personas = personaJuridicaService.findByRazonSocial(razonSocial.trim());
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<PersonaJuridicaResponseDTO> getPersonaJuridicaById(@PathVariable Integer id) {
        PersonaJuridicaResponseDTO persona = personaJuridicaService.findById(id);
        return ResponseEntity.ok(persona);
    }

    @PostMapping
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<PersonaJuridicaResponseDTO> createPersonaJuridica(@Valid @RequestBody PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        PersonaJuridicaResponseDTO nuevaPersona = personaJuridicaService.save(personaJuridicaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersona);
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaJuridicaResponseDTO> patchPersonaJuridica(@PathVariable Integer id, @Valid @RequestBody PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        PersonaJuridicaResponseDTO personaActualizada = personaJuridicaService.patch(id, personaJuridicaRequestDTO);
        return ResponseEntity.ok(personaActualizada);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deletePersonaJuridica(@PathVariable Integer id) {
        personaJuridicaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
