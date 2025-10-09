package com.everywhere.backend.service.impl;

import com.everywhere.backend.service.DocumentoCobranzaPdfService;
import com.everywhere.backend.service.DocumentoCobranzaService;
import com.everywhere.backend.service.PdfService;
import com.everywhere.backend.model.dto.DocumentoCobranzaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;

@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private DocumentoCobranzaPdfService documentoCobranzaPdfService;
    
    @Autowired
    private DocumentoCobranzaService documentoCobranzaService;

    @Override
    public ByteArrayInputStream generatePdf(String tipo, Object... parametros) {
        switch (tipo.toLowerCase()) {
            case "documento_cobranza":
                if (parametros.length >= 4) {
                    int cotizacionId = (Integer) parametros[0];
                    String nroSerie = (String) parametros[1];
                    String fileVenta = (String) parametros[2];
                    Double costoEnvio = (Double) parametros[3];
                    return documentoCobranzaPdfService.generatePdf(cotizacionId, nroSerie, fileVenta, costoEnvio);
                }
                throw new IllegalArgumentException("Parametros insuficientes para documento de cobranza");
            case "reporte":
                throw new UnsupportedOperationException("Tipo de PDF reporte no implementado aun");
            case "factura":
                throw new UnsupportedOperationException("Tipo de PDF factura no implementado aun");
            default:
                throw new IllegalArgumentException("Tipo de PDF no soportado: " + tipo);
        }
    }

    @Override
    public ByteArrayInputStream generateDocumentoCobranzaPdf(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio) {
        // Modo anterior: solo generar DTO y PDF (no guardar en BD)
        return documentoCobranzaPdfService.generatePdf(cotizacionId, nroSerie, fileVenta, costoEnvio);
    }
    
    @Override
    public ByteArrayInputStream generateDocumentoCobranzaPdfWithSave(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio) {
        // Modo nuevo: guardar en BD y luego generar PDF
        DocumentoCobranzaDTO documento = documentoCobranzaService.generateAndSaveDocumentoCobranza(cotizacionId, nroSerie, fileVenta, costoEnvio);
        
        // Generar PDF usando el DTO (que ya incluye el número de documento generado)
        return documentoCobranzaPdfService.generatePdfFromDTO(documento);
    }
}
