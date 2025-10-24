package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;

import java.util.List;

public interface DetalleDocumentoService {

    List<DetalleDocumentoResponseDto> findByViajeroId(Integer viajeroId);
    DetalleDocumentoResponseDto findById(Integer id);
    DetalleDocumentoResponseDto save(DetalleDocumentoRequestDto dto);
    DetalleDocumentoResponseDto update(Integer id, DetalleDocumentoRequestDto dto);
    void delete(Integer id);
    List<DetalleDocumentoResponseDto> findAll();
    List<DetalleDocumentoResponseDto> findByDocumentoId(Integer documentoId);
    List<DetalleDocumentoResponseDto> findByNumero(String numero);


}
