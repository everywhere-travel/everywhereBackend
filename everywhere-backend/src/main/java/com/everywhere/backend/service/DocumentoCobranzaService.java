package com.everywhere.backend.service;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.everywhere.backend.model.dto.DocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.dto.DocumentoCobranzaUpdateDTO;

public interface DocumentoCobranzaService {
    DocumentoCobranzaResponseDTO createDocumentoCobranza(Integer cotizacionId, Integer personaJuridicaId, Integer sucursalId);
    ByteArrayInputStream generatePdf(Long documentoId);
    DocumentoCobranzaResponseDTO findById(Long id);
    DocumentoCobranzaResponseDTO findByNumero(String numero);
    List<DocumentoCobranzaResponseDTO> findAll();
    DocumentoCobranzaResponseDTO findByCotizacionId(Integer cotizacionId);
    DocumentoCobranzaResponseDTO patchDocumento(Long id, DocumentoCobranzaUpdateDTO documentoCobranzaUpdateDTO);
}