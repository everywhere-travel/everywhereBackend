package com.everywhere.backend.api;

import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.DocumentoCobranzaService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@AllArgsConstructor
@RequestMapping("/pdf")
public class PdfController {

    private final DocumentoCobranzaService documentoCobranzaService;

    @GetMapping("/documento-cobranza/{id}")
    @RequirePermission(module = "DOCUMENTOS_COBRANZA", permission = "READ")
    public ResponseEntity<InputStreamResource> generateDocumentoCobranzaPdf(@PathVariable Long id) {

        try {
            // Verificar que el documento existe usando el DTO
            com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO documentoDto = documentoCobranzaService.findById(id);
            
            if (documentoDto == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            
            ByteArrayInputStream pdfStream = documentoCobranzaService.generatePdf(id);

            if (pdfStream == null) return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=" + documentoDto.getNumero() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdfStream));

        } catch (Exception e) {
            System.err.println("Error generando PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}