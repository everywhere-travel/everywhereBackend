package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;

import java.util.List;

public interface DetalleDocumentoService {
    DetalleDocumentoResponseDto findById(Integer id);
    DetalleDocumentoResponseDto save(DetalleDocumentoRequestDto detalleDocumentoRequestDto);
    DetalleDocumentoResponseDto update(Integer id, DetalleDocumentoRequestDto detalleDocumentoRequestDto);
    void delete(Integer id);
    List<DetalleDocumentoResponseDto> findAll();
    List<DetalleDocumentoResponseDto> findByDocumentoId(Integer documentoId);
    List<DetalleDocumentoResponseDto> findByNumero(String numero);
    List<DetalleDocumentoResponseDto> findByPersonaNaturalId(Integer personaNaturalId);
}