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
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getAllPersonasNaturales() {
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findAll();
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaNaturalResponseDTO> getPersonaNaturalById(@PathVariable Integer id) {
        PersonaNaturalResponseDTO persona = personaNaturalService.findById(id);
        return ResponseEntity.ok(persona);
    }

    @GetMapping("/documento")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByDocumento(@RequestParam String documento) {
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByDocumento(documento);
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/nombres")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByNombres(@RequestParam String nombres) {
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByNombres(nombres);
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/apellidos")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByApellidos(@RequestParam String apellidos) {
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByApellidos(apellidos);
        return ResponseEntity.ok(personas);
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
