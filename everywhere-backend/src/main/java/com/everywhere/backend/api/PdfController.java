package com.everywhere.backend.api;

import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/documento-cobranza")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public ResponseEntity<byte[]> generateDocumentoCobranzaPdf(
            @RequestParam int cotizacionId,
            @RequestParam String nroSerie,
            @RequestParam String fileVenta,
            @RequestParam Double costoEnvio,
            @RequestParam(defaultValue = "true") boolean saveToDatabase) {

        try {
            ByteArrayInputStream pdfStream;
            
            if (saveToDatabase) {
                // Opción 1: Guardar en BD y generar PDF
                pdfStream = pdfService.generateDocumentoCobranzaPdfWithSave(cotizacionId, nroSerie, fileVenta, costoEnvio);
            } else {
                // Opción 2: Solo generar PDF sin guardar (modo anterior)
                pdfStream = pdfService.generateDocumentoCobranzaPdf(cotizacionId, nroSerie, fileVenta, costoEnvio);
            }

            if (pdfStream == null) {
                return ResponseEntity.noContent().build();
            }

            byte[] pdfBytes = pdfStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", 
                "documento_cobranza_" + nroSerie + "_" + fileVenta + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            // Log del error para debugging
            System.err.println("Error generando PDF de documento de cobranza: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Método genérico (alternativo)
    @PostMapping("/generar")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public ResponseEntity<byte[]> generatePdf(
            @RequestParam String tipo,
            @RequestBody Object[] parametros) {

        try {
            ByteArrayInputStream pdfStream = pdfService.generatePdf(tipo, parametros);

            if (pdfStream == null) {
                return ResponseEntity.noContent().build();
            }

            byte[] pdfBytes = pdfStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", tipo + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}