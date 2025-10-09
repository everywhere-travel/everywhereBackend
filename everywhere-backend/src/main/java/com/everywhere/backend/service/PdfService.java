package com.everywhere.backend.service;

import java.io.ByteArrayInputStream;

public interface PdfService {
    
    // Método genérico para cualquier tipo de PDF
    ByteArrayInputStream generatePdf(String tipo, Object... parametros);
    
    // Métodos específicos (delegación)
    ByteArrayInputStream generateDocumentoCobranzaPdf(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio);
    
    // Método con persistencia
    ByteArrayInputStream generateDocumentoCobranzaPdfWithSave(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio);
}
