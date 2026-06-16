package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.AsientoContableRequestDTO;
import com.everywhere.backend.model.dto.AsientoContableResponseDTO;
import com.everywhere.backend.service.AsientoContableService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/asientos-contables")
@RequiredArgsConstructor
public class AsientoContableController {

    private final AsientoContableService asientoService;

    @GetMapping
    public ResponseEntity<List<AsientoContableResponseDTO>> listar() {
        return ResponseEntity.ok(asientoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsientoContableResponseDTO> obtenerPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(asientoService.obtenerPorId(id));
    }

    @GetMapping("/origen")
    public ResponseEntity<List<AsientoContableResponseDTO>> listarPorOrigen(
            @RequestParam String origen,
            @RequestParam Integer origenId) {

        return ResponseEntity.ok(
                asientoService.listarPorOrigen(origen, origenId)
        );
    }

    @PostMapping
    public ResponseEntity<AsientoContableResponseDTO> crear(
            @RequestBody AsientoContableRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(asientoService.crear(request));
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<Void> anular(@PathVariable Integer id) {
        asientoService.anular(id);
        return ResponseEntity.noContent().build();
    }
}