package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaRequestDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaResponseDTO;
import com.everywhere.backend.service.DetalleDocumentoCobranzaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalle-documento-cobranza")
@RequiredArgsConstructor
public class DetalleDocumentoCobranzaController {

    private final DetalleDocumentoCobranzaService detalleService;

    @GetMapping
    public ResponseEntity<List<DetalleDocumentoCobranzaResponseDTO>> getAllDetalles() {
        List<DetalleDocumentoCobranzaResponseDTO> detalles = detalleService.findAll();
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleDocumentoCobranzaResponseDTO> getDetalleById(@PathVariable Long id) {
        DetalleDocumentoCobranzaResponseDTO detalle = detalleService.findById(id);
        return ResponseEntity.ok(detalle);
    }

    @GetMapping("/documento-cobranza/{documentoId}")
    public ResponseEntity<List<DetalleDocumentoCobranzaResponseDTO>> getDetallesByDocumentoCobranza(@PathVariable Long documentoId) {
        List<DetalleDocumentoCobranzaResponseDTO> detalles = detalleService.findByDocumentoCobranzaId(documentoId);
        return ResponseEntity.ok(detalles);
    }

    @PostMapping
    public ResponseEntity<DetalleDocumentoCobranzaResponseDTO> createDetalle(@Valid @RequestBody DetalleDocumentoCobranzaRequestDTO dto) {
        DetalleDocumentoCobranzaResponseDTO detalle = detalleService.save(dto);
        return new ResponseEntity<>(detalle, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleDocumentoCobranzaResponseDTO> updateDetalle(
            @PathVariable Long id, 
            @Valid @RequestBody DetalleDocumentoCobranzaRequestDTO dto) {
        DetalleDocumentoCobranzaResponseDTO detalle = detalleService.update(id, dto);
        return ResponseEntity.ok(detalle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetalle(@PathVariable Long id) {
        detalleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
