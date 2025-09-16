package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.PersonaRequestDTO;
import com.everywhere.backend.model.dto.PersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaDisplayDto;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.PersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaResponseDTO>> getAllPersonas() {
        List<PersonaResponseDTO> personas = personaService.findAll();
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<PersonaResponseDTO> getPersonaById(@PathVariable Integer id) {
        PersonaResponseDTO persona = personaService.findById(id);
        return ResponseEntity.ok(persona);
    }

    @GetMapping("/email")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaResponseDTO>> getPersonasByEmail(@RequestParam String email) {
        List<PersonaResponseDTO> personas = personaService.findByEmail(email);
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/telefono")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaResponseDTO>> getPersonasByTelefono(@RequestParam String telefono) {
        List<PersonaResponseDTO> personas = personaService.findByTelefono(telefono);
        return ResponseEntity.ok(personas);
    }

    @PostMapping
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<PersonaResponseDTO> createPersona(@Valid @RequestBody PersonaRequestDTO personaRequestDTO) {
        PersonaResponseDTO nuevaPersona = personaService.save(personaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersona);
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaResponseDTO> updatePersona(@PathVariable Integer id, @Valid @RequestBody PersonaRequestDTO personaRequestDTO) {
        PersonaResponseDTO personaActualizada = personaService.update(id, personaRequestDTO);
        return ResponseEntity.ok(personaActualizada);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deletePersona(@PathVariable Integer id) {
        personaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{personaId}/NaturalOrJuridica")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public PersonaDisplayDto findPersonaNaturalOrJuridicaById(@PathVariable Integer personaId) {
        return personaService.findPersonaNaturalOrJuridicaById(personaId);
    }
}
