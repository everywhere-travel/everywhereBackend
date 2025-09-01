package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;

import java.util.List;
import java.util.Optional;

public interface CotizacionService {

    CotizacionResponseDto create(CotizacionRequestDto dto, Integer personaId);
    Optional<CotizacionResponseDto> findById(Integer id);
    List<CotizacionResponseDto> findAll();
    CotizacionResponseDto update(Integer id, CotizacionRequestDto dto);
    void delete(Integer id);

    // Métodos para asignar relaciones por ID
    CotizacionResponseDto setFormaPagoById(Integer cotizacionId, Integer formaPagoId);
    CotizacionResponseDto setEstadoCotizacionById(Integer cotizacionId, Integer estadoId);
    CotizacionResponseDto setCounterById(Integer cotizacionId, Integer counterId);
    CotizacionResponseDto setSucursalById(Integer cotizacionId, Integer sucursalId);
    CotizacionResponseDto setCarpetaById(Integer cotizacionId, Integer carpetaId);
    CotizacionResponseDto setPersonasById(Integer cotizacionId, Integer personasId);
}

