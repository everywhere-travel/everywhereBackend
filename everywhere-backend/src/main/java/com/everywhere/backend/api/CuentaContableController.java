package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CuentaContableRequestDTO;
import com.everywhere.backend.model.dto.CuentaContableResponseDTO;
import com.everywhere.backend.service.CuentaContableService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas-contables")
@RequiredArgsConstructor
public class CuentaContableController {

    private final CuentaContableService cuentaService;

    @GetMapping
    public ResponseEntity<List<CuentaContableResponseDTO>> listar() {
        return ResponseEntity.ok(cuentaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaContableResponseDTO> obtenerPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(cuentaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CuentaContableResponseDTO> crear(
            @RequestBody CuentaContableRequestDTO request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cuentaService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaContableResponseDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody CuentaContableRequestDTO request) {

        return ResponseEntity.ok(cuentaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        cuentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}