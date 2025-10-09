package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DocumentoCobranzaDTO;
import java.io.ByteArrayInputStream;

public interface DocumentoCobranzaPdfService {
    
    ByteArrayInputStream generatePdf(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio);
    
    ByteArrayInputStream generatePdfFromDTO(DocumentoCobranzaDTO documento);
}