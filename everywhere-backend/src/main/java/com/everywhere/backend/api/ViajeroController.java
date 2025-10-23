package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.ViajeroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viajeros")
@RequiredArgsConstructor
public class ViajeroController {

    private final ViajeroService viajeroService;

    @GetMapping
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getAllViajeros() {
        List<ViajeroResponseDTO> viajeros = viajeroService.findAll();
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/nacionalidad")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajeroByNacionalidad(@RequestParam String nacionalidad) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByNacionalidad(nacionalidad.trim());
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/residencia")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajeroByResidencia(@RequestParam String residencia) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByResidencia(residencia.trim());
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<ViajeroResponseDTO> getViajeroById(@PathVariable Integer id) {
        ViajeroResponseDTO viajero = viajeroService.findById(id);
        return ResponseEntity.ok(viajero);
    }

    @PostMapping
    @RequirePermission(module = "VIAJEROS", permission = "CREATE")
    public ResponseEntity<ViajeroResponseDTO> createViajero(@Valid @RequestBody ViajeroRequestDTO viajeroRequestDTO) {
        ViajeroResponseDTO nuevoViajero = viajeroService.save(viajeroRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoViajero);
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "UPDATE")
    public ResponseEntity<ViajeroResponseDTO> patch(@PathVariable Integer id, @Valid @RequestBody ViajeroRequestDTO viajeroRequestDTO) {
        ViajeroResponseDTO viajeroActualizado = viajeroService.patch(id, viajeroRequestDTO);
        return ResponseEntity.ok(viajeroActualizado);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "DELETE")
    public ResponseEntity<Void> deleteViajero(@PathVariable Integer id) {
        viajeroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
