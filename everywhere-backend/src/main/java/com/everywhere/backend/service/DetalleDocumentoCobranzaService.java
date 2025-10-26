package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaRequestDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaResponseDTO;

import java.util.List;

public interface DetalleDocumentoCobranzaService {
    
    List<DetalleDocumentoCobranzaResponseDTO> findAll();
    DetalleDocumentoCobranzaResponseDTO findById(Long id);
    List<DetalleDocumentoCobranzaResponseDTO> findByDocumentoCobranzaId(Long documentoId);
    DetalleDocumentoCobranzaResponseDTO save(DetalleDocumentoCobranzaRequestDTO dto);
    DetalleDocumentoCobranzaResponseDTO update(Long id, DetalleDocumentoCobranzaRequestDTO dto);
    void deleteById(Long id);
}