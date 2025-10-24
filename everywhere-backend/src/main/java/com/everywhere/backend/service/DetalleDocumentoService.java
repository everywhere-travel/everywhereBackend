package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DetalleDocumentoRequestDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDTO;

import java.util.List;

public interface DetalleDocumentoService {

    List<DetalleDocumentoResponseDTO> findByViajeroId(Integer viajeroId);
    DetalleDocumentoResponseDTO findById(Integer id);
    DetalleDocumentoResponseDTO save(DetalleDocumentoRequestDTO dto);
    DetalleDocumentoResponseDTO update(Integer id, DetalleDocumentoRequestDTO dto);
    void delete(Integer id);
    List<DetalleDocumentoResponseDTO> findAll();
    List<DetalleDocumentoResponseDTO> findByDocumentoId(Integer documentoId);
    List<DetalleDocumentoResponseDTO> findByNumero(String numero);


}
