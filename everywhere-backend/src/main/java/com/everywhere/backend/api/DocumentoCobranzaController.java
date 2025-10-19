package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.DocumentoCobranzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documentos-cobranza")
public class DocumentoCobranzaController {

    @Autowired
    private DocumentoCobranzaService documentoCobranzaService;

    @PostMapping
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "CREATE")
    public ResponseEntity<?> createDocumentoCobranza(
            @RequestParam Integer cotizacionId,
            @RequestParam String fileVenta,
            @RequestParam Double costoEnvio) {
        
        try {
            DocumentoCobranzaResponseDTO documento = documentoCobranzaService.createDocumentoCobranza(cotizacionId, fileVenta, costoEnvio);
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
            
            if (documento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento no encontrado");
            }
            
            return ResponseEntity.ok(documento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/numero/{numero}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<?> getDocumentoByNumero(@PathVariable String numero) {
        try {
            DocumentoCobranzaResponseDTO documento = documentoCobranzaService.findByNumero(numero);
            
            if (documento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento no encontrado");
            }
            
            return ResponseEntity.ok(documento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cotizacion/{cotizacionId}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<?> getDocumentoByCotizacion(@PathVariable Integer cotizacionId) {
        try {
            DocumentoCobranzaResponseDTO documento = documentoCobranzaService.findByCotizacionId(cotizacionId);
            
            if (documento == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe documento de cobranza para la cotización " + cotizacionId);
            }
            
            return ResponseEntity.ok(documento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
