package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DocumentoCobranzaDTO;
import com.everywhere.backend.model.entity.DocumentoCobranza;

public interface DocumentoCobranzaService {
    
    /**
     * Genera documento de cobranza DTO (efímero) para PDF
     */
    DocumentoCobranzaDTO generateDocumentoCobranza(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio);
    
    /**
     * Genera y guarda documento de cobranza en BD, luego retorna DTO
     */
    DocumentoCobranzaDTO generateAndSaveDocumentoCobranza(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio);
    
    /**
     * Convierte entidad a DTO
     */
    DocumentoCobranzaDTO convertToDTO(DocumentoCobranza entity);
}
