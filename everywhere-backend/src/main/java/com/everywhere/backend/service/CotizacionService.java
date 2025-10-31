package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;
import com.everywhere.backend.model.dto.CotizacionConDetallesResponseDTO;

import java.util.List; 

public interface CotizacionService {
    CotizacionResponseDto create(CotizacionRequestDto dto, Integer personaId);
    CotizacionResponseDto findById(Integer id);
    List<CotizacionResponseDto> findAll();
    CotizacionResponseDto update(Integer id, CotizacionRequestDto dto);
    void delete(Integer id);
    CotizacionConDetallesResponseDTO findByIdWithDetalles(Integer id);
    List<CotizacionResponseDto> findCotizacionesSinLiquidacion();
}