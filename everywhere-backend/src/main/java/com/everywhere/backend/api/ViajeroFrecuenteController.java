package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ViajeroFrecuenteRequestDto;
import com.everywhere.backend.model.dto.ViajeroFrecuenteResponseDto;
import com.everywhere.backend.service.ViajeroFrecuenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/viajeros-frecuentes")
public class ViajeroFrecuenteController {

    private final ViajeroFrecuenteService viajeroFrecuenteService;

    public ViajeroFrecuenteController(ViajeroFrecuenteService viajeroFrecuenteService) {
        this.viajeroFrecuenteService = viajeroFrecuenteService;
    }

    // Crear viajero frecuente asociando a un viajero existente
    @PostMapping("/{viajeroId}")
    public ResponseEntity<ViajeroFrecuenteResponseDto> crear(
            @PathVariable Integer viajeroId,
            @RequestBody ViajeroFrecuenteRequestDto dto) {
        return ResponseEntity.ok(viajeroFrecuenteService.crear(viajeroId, dto));
    }

    // Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<ViajeroFrecuenteResponseDto> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(viajeroFrecuenteService.buscarPorId(id));
    }

    // Listar todos los viajeros frecuentes de un viajero
    @GetMapping("/viajero/{viajeroId}")
    public ResponseEntity<List<ViajeroFrecuenteResponseDto>> listarPorViajero(@PathVariable Integer viajeroId) {
        return ResponseEntity.ok(viajeroFrecuenteService.listarPorViajero(viajeroId));
    }

    // Actualizar
    @PutMapping("/{id}")
    public ResponseEntity<ViajeroFrecuenteResponseDto> actualizar(
            @PathVariable Integer id,
            @RequestBody ViajeroFrecuenteRequestDto dto) {
        return ResponseEntity.ok(viajeroFrecuenteService.actualizar(id, dto));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        viajeroFrecuenteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Buscar todos los viajeros frecuentes por ID de viajero
    @GetMapping("/search/{viajeroId}")
    public ResponseEntity<List<ViajeroFrecuenteResponseDto>> buscarPorViajeroId(
            @PathVariable Integer viajeroId) {
        return ResponseEntity.ok(viajeroFrecuenteService.buscarPorViajeroId(viajeroId));
    }

}
