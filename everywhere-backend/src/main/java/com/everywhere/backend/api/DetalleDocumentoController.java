package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;
import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.service.DetalleDocumentoService;
import com.everywhere.backend.security.RequirePermission;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalle-documento")
@RequiredArgsConstructor
public class DetalleDocumentoController {

    private final DetalleDocumentoService detalleDocumentoService;

    @GetMapping
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public ResponseEntity<List<DetalleDocumentoResponseDto>> findAll() {
        return ResponseEntity.ok(detalleDocumentoService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public ResponseEntity<DetalleDocumentoResponseDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(detalleDocumentoService.findById(id));
    }

    @GetMapping("/documento/{documentoId}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public ResponseEntity<List<DetalleDocumentoResponseDto>> findByDocumentoId(@PathVariable Integer documentoId) {
        return ResponseEntity.ok(detalleDocumentoService.findByDocumentoId(documentoId));
    }

    @GetMapping("/numero/{numero}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public ResponseEntity<List<DetalleDocumentoResponseDto>> findByNumero(@PathVariable String numero) {
        return ResponseEntity.ok(detalleDocumentoService.findByNumero(numero));
    }

    @PostMapping
    @RequirePermission(module = "DOCUMENTOS", permission = "CREATE")
    public ResponseEntity<DetalleDocumentoResponseDto> save(@RequestBody DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleDocumentoService.save(detalleDocumentoRequestDto));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS", permission = "UPDATE")
    public ResponseEntity<DetalleDocumentoResponseDto> update(@PathVariable Integer id, @RequestBody DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        return ResponseEntity.ok(detalleDocumentoService.update(id, detalleDocumentoRequestDto));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        detalleDocumentoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}