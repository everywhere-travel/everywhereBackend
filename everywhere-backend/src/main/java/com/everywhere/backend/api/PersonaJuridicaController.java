package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.PersonaJuridicaRequestDTO;
import com.everywhere.backend.model.dto.PersonaJuridicaResponseDTO;
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
    public ResponseEntity<List<PersonaJuridicaResponseDTO>> getAllPersonasJuridicas(
            @RequestParam(required = false) String ruc,
            @RequestParam(required = false) String razonSocial,
            @RequestParam(name = "razon-social", required = false) String razonSocialConGuion) {

        // Si se proporciona RUC, buscar por RUC
        if (ruc != null && !ruc.trim().isEmpty()) {
            List<PersonaJuridicaResponseDTO> personas = personaJuridicaService.findByRuc(ruc.trim());
            return ResponseEntity.ok(personas);
        }

        // Si se proporciona razón social (camelCase), buscar por razón social
        if (razonSocial != null && !razonSocial.trim().isEmpty()) {
            List<PersonaJuridicaResponseDTO> personas = personaJuridicaService.findByRazonSocial(razonSocial.trim());
            return ResponseEntity.ok(personas);
        }

        // Si se proporciona razón social (con guión), buscar por razón social
        if (razonSocialConGuion != null && !razonSocialConGuion.trim().isEmpty()) {
            List<PersonaJuridicaResponseDTO> personas = personaJuridicaService.findByRazonSocial(razonSocialConGuion.trim());
            return ResponseEntity.ok(personas);
        }

        // Si no se proporciona ningún parámetro, devolver todos
        List<PersonaJuridicaResponseDTO> personas = personaJuridicaService.findAll();
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaJuridicaResponseDTO> getPersonaJuridicaById(@PathVariable Integer id) {
        PersonaJuridicaResponseDTO persona = personaJuridicaService.findById(id);
        return ResponseEntity.ok(persona);
    }

    @GetMapping("/ruc")
    public ResponseEntity<List<PersonaJuridicaResponseDTO>> getPersonasJuridicasByRuc(@RequestParam String ruc) {
        List<PersonaJuridicaResponseDTO> personas = personaJuridicaService.findByRuc(ruc);
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/razon-social")
    public ResponseEntity<List<PersonaJuridicaResponseDTO>> getPersonasJuridicasByRazonSocial(@RequestParam String razonSocial) {
        List<PersonaJuridicaResponseDTO> personas = personaJuridicaService.findByRazonSocial(razonSocial);
        return ResponseEntity.ok(personas);
    }

    @PostMapping
    public ResponseEntity<PersonaJuridicaResponseDTO> createPersonaJuridica(@Valid @RequestBody PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        PersonaJuridicaResponseDTO nuevaPersona = personaJuridicaService.save(personaJuridicaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersona);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaJuridicaResponseDTO> updatePersonaJuridica(@PathVariable Integer id, @Valid @RequestBody PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        PersonaJuridicaResponseDTO personaActualizada = personaJuridicaService.update(id, personaJuridicaRequestDTO);
        return ResponseEntity.ok(personaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonaJuridica(@PathVariable Integer id) {
        personaJuridicaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
