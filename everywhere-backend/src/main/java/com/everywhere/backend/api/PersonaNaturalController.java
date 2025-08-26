package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.service.PersonaNaturalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personas-naturales")
@RequiredArgsConstructor
public class PersonaNaturalController {

    private final PersonaNaturalService personaNaturalService;

    @GetMapping
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getAllPersonasNaturales(
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String nombres,
            @RequestParam(required = false) String apellidos) {

        // Si se proporciona documento, buscar por documento
        if (documento != null && !documento.trim().isEmpty()) {
            List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByDocumento(documento.trim());
            return ResponseEntity.ok(personas);
        }

        // Si se proporciona nombres, buscar por nombres
        if (nombres != null && !nombres.trim().isEmpty()) {
            List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByNombres(nombres.trim());
            return ResponseEntity.ok(personas);
        }

        // Si se proporciona apellidos, buscar por apellidos
        if (apellidos != null && !apellidos.trim().isEmpty()) {
            List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByApellidos(apellidos.trim());
            return ResponseEntity.ok(personas);
        }

        // Si no se proporciona ningún parámetro, devolver todos
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findAll();
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaNaturalResponseDTO> getPersonaNaturalById(@PathVariable Integer id) {
        PersonaNaturalResponseDTO persona = personaNaturalService.findById(id);
        return ResponseEntity.ok(persona);
    }

    @PostMapping
    public ResponseEntity<PersonaNaturalResponseDTO> createPersonaNatural(@Valid @RequestBody PersonaNaturalRequestDTO personaNaturalRequestDTO) {
        PersonaNaturalResponseDTO nuevaPersona = personaNaturalService.save(personaNaturalRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersona);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaNaturalResponseDTO> updatePersonaNatural(@PathVariable Integer id, @Valid @RequestBody PersonaNaturalRequestDTO personaNaturalRequestDTO) {
        PersonaNaturalResponseDTO personaActualizada = personaNaturalService.update(id, personaNaturalRequestDTO);
        return ResponseEntity.ok(personaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonaNatural(@PathVariable Integer id) {
        personaNaturalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
