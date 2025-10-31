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
    public ResponseEntity<DocumentoCobranzaResponseDTO> createDocumentoCobranza(@RequestParam Integer cotizacionId) { 
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoCobranzaService.createDocumentoCobranza(cotizacionId));
    }

    @GetMapping
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<List<DocumentoCobranzaResponseDTO>> getAllDocumentos() { 
        return ResponseEntity.ok(documentoCobranzaService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<?> getDocumentoById(@PathVariable Long id) { 
        return ResponseEntity.ok(documentoCobranzaService.findById(id));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "UPDATE")
    public ResponseEntity<?> updateDocumento(@PathVariable Long id,
            @Valid @RequestBody DocumentoCobranzaUpdateDTO documentoCobranzaUpdateDTO) {
        return ResponseEntity.ok(documentoCobranzaService.patchDocumento(id, documentoCobranzaUpdateDTO));
    }
}