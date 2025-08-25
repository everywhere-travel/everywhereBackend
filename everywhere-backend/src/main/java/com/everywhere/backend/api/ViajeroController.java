package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.service.ViajeroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/viajeros")
@RequiredArgsConstructor
public class ViajeroController {

    private final ViajeroService viajeroService;

    @GetMapping
    public ResponseEntity<List<ViajeroResponseDTO>> getAllViajeros() {
        List<ViajeroResponseDTO> viajeros = viajeroService.findAll();
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViajeroResponseDTO> getViajeroById(@PathVariable Integer id) {
        ViajeroResponseDTO viajero = viajeroService.findById(id);
        return ResponseEntity.ok(viajero);
    }

    @GetMapping("/nombres")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajerosByNombres(@RequestParam String nombres) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByNombres(nombres);
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/numero-documento")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajerosByNumeroDocumento(@RequestParam String numeroDocumento) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByNumeroDocumento(numeroDocumento);
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/nacionalidad")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajerosByNacionalidad(@RequestParam String nacionalidad) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByNacionalidad(nacionalidad);
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/residencia")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajerosByResidencia(@RequestParam String residencia) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByResidencia(residencia);
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/fecha-vencimiento")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajerosByFechaVencimiento(@RequestParam LocalDate fechaVencimientoDocumento) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByFechaVencimientoDocumento(fechaVencimientoDocumento);
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/fecha-vencimiento-rango")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajerosByFechaVencimientoRango(@RequestParam LocalDate fechaInicio,
                                                                                       @RequestParam LocalDate fechaFin) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByFechaVencimientoDocumentoBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(viajeros);
    }

    @PostMapping
    public ResponseEntity<ViajeroResponseDTO> createViajero(@Valid @RequestBody ViajeroRequestDTO viajeroRequestDTO) {
        ViajeroResponseDTO nuevoViajero = viajeroService.save(viajeroRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoViajero);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViajeroResponseDTO> updateViajero(@PathVariable Integer id, @Valid @RequestBody ViajeroRequestDTO viajeroRequestDTO) {
        ViajeroResponseDTO viajeroActualizado = viajeroService.update(id, viajeroRequestDTO);
        return ResponseEntity.ok(viajeroActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteViajero(@PathVariable Integer id) {
        viajeroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
