package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ViajeroFrecuenteRequestDTO;
import com.everywhere.backend.model.dto.ViajeroFrecuenteResponseDTO;
import com.everywhere.backend.security.RequirePermission;
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
    @RequirePermission(module = "VIAJEROS", permission = "CREATE")
    public ResponseEntity<ViajeroFrecuenteResponseDTO> crear(
            @PathVariable Integer viajeroId,
            @RequestBody ViajeroFrecuenteRequestDTO dto) {
        return ResponseEntity.ok(viajeroFrecuenteService.crear(viajeroId, dto));
    }

    // Buscar por ID
    @GetMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<ViajeroFrecuenteResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(viajeroFrecuenteService.buscarPorId(id));
    }

    // Listar todos los viajeros frecuentes de un viajero
    @GetMapping("/viajero/{viajeroId}")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroFrecuenteResponseDTO>> listarPorViajero(@PathVariable Integer viajeroId) {
        return ResponseEntity.ok(viajeroFrecuenteService.listarPorViajero(viajeroId));
    }

    // Actualizar
    @PutMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "UPDATE")
    public ResponseEntity<ViajeroFrecuenteResponseDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody ViajeroFrecuenteRequestDTO dto) {
        return ResponseEntity.ok(viajeroFrecuenteService.actualizar(id, dto));
    }

    // Eliminar
    @DeleteMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "DELETE")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        viajeroFrecuenteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Buscar todos los viajeros frecuentes por ID de viajero
    @GetMapping("/search/{viajeroId}")
    @RequirePermission(module = "VIAJEROS", permission = "READ")
    public ResponseEntity<List<ViajeroFrecuenteResponseDTO>> buscarPorViajeroId(
            @PathVariable Integer viajeroId) {
        return ResponseEntity.ok(viajeroFrecuenteService.buscarPorViajeroId(viajeroId));
    }
}
