package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.ViajeroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/viajeros")
@RequiredArgsConstructor
public class ViajeroController {

    private final ViajeroService viajeroService;
    private final ViajeroRepository viajeroRepository;

    @GetMapping
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getAllViajeros() {
        List<ViajeroResponseDTO> viajeros = viajeroService.findAll();
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/nombres")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajeroByNombres(@RequestParam String nombres) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByNombres(nombres.trim());
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/numeroDocumento")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajeroByNumDoc(@RequestParam String numeroDocumento) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByNumeroDocumento(numeroDocumento.trim());
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

    @GetMapping("/fecha-vencimiento")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajerosByFechaVencimiento(@RequestParam LocalDate fechaVencimientoDocumento) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByFechaVencimientoDocumento(fechaVencimientoDocumento);
        return ResponseEntity.ok(viajeros);
    }

    @GetMapping("/fecha-vencimiento-rango")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroResponseDTO>> getViajerosByFechaVencimientoRango(@RequestParam LocalDate fechaInicio,
                                                                                       @RequestParam LocalDate fechaFin) {
        List<ViajeroResponseDTO> viajeros = viajeroService.findByFechaVencimientoDocumentoBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(viajeros);
    }

    @PostMapping
    @RequirePermission(module = "VIAJEROS", permission = "CREATE")
    public ResponseEntity<ViajeroResponseDTO> createViajero(@Valid @RequestBody ViajeroRequestDTO viajeroRequestDTO) {
        ViajeroResponseDTO nuevoViajero = viajeroService.save(viajeroRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoViajero);
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "UPDATE")
    public ResponseEntity<ViajeroResponseDTO> updateViajero(@PathVariable Integer id, @Valid @RequestBody ViajeroRequestDTO viajeroRequestDTO) {
        ViajeroResponseDTO viajeroActualizado = viajeroService.update(id, viajeroRequestDTO);
        return ResponseEntity.ok(viajeroActualizado);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "DELETE")
    public ResponseEntity<Void> deleteViajero(@PathVariable Integer id) {
        viajeroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/export/excelCosta")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<InputStreamResource> exportToExcelCosta(@RequestBody List<Integer> viajeroIds) throws IOException {

        List<Viajero> viajerosParaExportar = viajeroRepository.findAllById(viajeroIds);

        // Se llama al servicio (esto está correcto)
        ByteArrayInputStream in = viajeroService.exportToExcelCosta(viajerosParaExportar);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte_viajeros.xlsx");

        // El cuerpo del método no cambia, ya que está correcto
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in)); // <-- Ahora esto coincide con el tipo de retorno
    }
}
