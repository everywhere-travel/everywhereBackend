package com.everywhere.backend.service;

import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;

import java.util.List;

public interface DocumentoCobranzaPersistService {
    
    /**
     * Guarda un documento de cobranza con sus detalles en la base de datos
     */
    DocumentoCobranza saveDocumentoCobranza(int cotizacionId, String nroSerie, String fileVenta, Double costoEnvio);
    
    /**
     * Busca un documento de cobranza por ID
     */
    DocumentoCobranza findById(Long id);
    
    /**
     * Busca un documento de cobranza por número
     */
    DocumentoCobranza findByNumero(String numero);
    
    /**
     * Genera el siguiente número de documento de cobranza
     */
    String generateNextDocumentNumber();
    
    /**
     * Lista todos los documentos de cobranza
     */
    List<DocumentoCobranza> findAll();
}