package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.dto.PersonaNaturalViajeroDTO; 
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

    @GetMapping("/apellidos-paterno")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByApellidoPaterno(@RequestParam String apellidos) {
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByApellidosPaternos(apellidos.trim());
        return ResponseEntity.ok(personas);
    } 

    @GetMapping("/apellidos-materno")
    @RequirePermission(module = "PERSONAS", permission = "READ")
    public ResponseEntity<List<PersonaNaturalResponseDTO>> getPersonasNaturalesByApellidoMaterno(@RequestParam String apellidos) {
        List<PersonaNaturalResponseDTO> personas = personaNaturalService.findByApellidosMaternos(apellidos.trim());
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

    @PatchMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaNaturalResponseDTO> patchPersonaNatural(@PathVariable Integer id, @RequestBody PersonaNaturalRequestDTO personaNaturalRequestDTO) {
        PersonaNaturalResponseDTO personaActualizada = personaNaturalService.patch(id, personaNaturalRequestDTO);
        return ResponseEntity.ok(personaActualizada);
    }

    @PatchMapping("/{id}/viajero")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaNaturalResponseDTO> patchAsociarViajero(@PathVariable Integer id, @RequestBody PersonaNaturalViajeroDTO personaNaturalViajeroDTO) {
        PersonaNaturalResponseDTO personaActualizada = personaNaturalService.asociarViajero(id, personaNaturalViajeroDTO.getViajeroId());
        return ResponseEntity.ok(personaActualizada);
    }

    @DeleteMapping("/{id}/viajero")
    @RequirePermission(module = "PERSONAS", permission = "UPDATE")
    public ResponseEntity<PersonaNaturalResponseDTO> desasociarViajero(@PathVariable Integer id) {
        PersonaNaturalResponseDTO personaActualizada = personaNaturalService.desasociarViajero(id);
        return ResponseEntity.ok(personaActualizada);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PERSONAS", permission = "DELETE")
    public ResponseEntity<Void> deletePersonaNatural(@PathVariable Integer id) {
        personaNaturalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}