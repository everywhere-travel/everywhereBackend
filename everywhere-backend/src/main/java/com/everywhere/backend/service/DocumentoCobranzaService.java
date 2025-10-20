package com.everywhere.backend.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.everywhere.backend.model.dto.DocumentoCobranzaDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaUpdateDTO;
import com.everywhere.backend.model.entity.DocumentoCobranza;

public interface DocumentoCobranzaService {
    
    DocumentoCobranzaResponseDTO createDocumentoCobranza(Integer cotizacionId, String fileVenta, Double costoEnvio);
    ByteArrayInputStream generatePdf(Long documentoId);
    DocumentoCobranzaDTO convertToDTO(DocumentoCobranza entity);
    DocumentoCobranzaResponseDTO convertToResponseDTO(DocumentoCobranza entity);
    DocumentoCobranzaResponseDTO findById(Long id);
    DocumentoCobranzaResponseDTO findByNumero(String numero);
    List<DocumentoCobranzaResponseDTO> findAll();
    DocumentoCobranzaResponseDTO findByCotizacionId(Integer cotizacionId);
    DocumentoCobranzaResponseDTO updateDocumento(Long id, DocumentoCobranzaUpdateDTO updateDTO);
}
