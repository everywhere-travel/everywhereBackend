package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaUpdateDTO;
import com.everywhere.backend.security.RequirePermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import com.everywhere.backend.service.DocumentoCobranzaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documentos-cobranza")
public class DocumentoCobranzaController {

    private final DocumentoCobranzaService documentoCobranzaService;

    @PostMapping
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "CREATE")
    public ResponseEntity<?> createDocumentoCobranza(@RequestParam Integer cotizacionId) {
        try {
            DocumentoCobranzaResponseDTO documento = documentoCobranzaService.createDocumentoCobranza(cotizacionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(documento);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear documento de cobranza");
        }
    }

    @GetMapping
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<List<DocumentoCobranzaResponseDTO>> getAllDocumentos() {
        try {
            List<DocumentoCobranzaResponseDTO> documentos = documentoCobranzaService.findAll();
            return ResponseEntity.ok(documentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<?> getDocumentoById(@PathVariable Long id) {
        try {
            DocumentoCobranzaResponseDTO documento = documentoCobranzaService.findById(id);

            if (documento == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento no encontrado");
            return ResponseEntity.ok(documento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "UPDATE")
    public ResponseEntity<?> updateDocumento(@PathVariable Long id,
            @Valid @RequestBody DocumentoCobranzaUpdateDTO documentoCobranzaUpdateDTO) {
        try {
            DocumentoCobranzaResponseDTO documentoCobranzaResponseDTO = documentoCobranzaService.patchDocumento(id, documentoCobranzaUpdateDTO);
            return ResponseEntity.ok(documentoCobranzaResponseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar documento de cobranza");
        }
    }
}