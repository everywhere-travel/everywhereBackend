package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.security.RequirePermission;
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
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getAllPersonasNaturales() {
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findAll();
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/documento")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByDocumento(@RequestParam String documento) {
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByDocumento(documento.trim());
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/nombres")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByNombre(@RequestParam String nombres) {
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByNombres(nombres.trim());
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/apellidos")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByApellido(@RequestParam String apellidos) {
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByApellidos(apellidos.trim());
        return ResponseEntity.ok(personas);
    } 

    @GetMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<PersonaNaturalResponseDTO> getPersonaNaturalById(@PathVariable Integer id) {
        PersonaNaturalResponseDTO persona = personaNaturalService.findById(id);
        return ResponseEntity.ok(persona);
    }

    @PostMapping
    @RequirePermission(module = "PERSONAS", permission = "CREATE")
    public ResponseEntity<PersonaNaturalResponseDTO> createPersonaNatural(@Valid @RequestBody PersonaNaturalRequestDTO personaNaturalRequestDTO) {
        PersonaNaturalResponseDTO nuevaPersona = personaNaturalService.save(personaNaturalRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersona);
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaNaturalResponseDTO> updatePersonaNatural(@PathVariable Integer id, @Valid @RequestBody PersonaNaturalRequestDTO personaNaturalRequestDTO) {
        PersonaNaturalResponseDTO personaActualizada = personaNaturalService.update(id, personaNaturalRequestDTO);
        return ResponseEntity.ok(personaActualizada);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deletePersonaNatural(@PathVariable Integer id) {
        personaNaturalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
