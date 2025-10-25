package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DetalleCotizacionRequestDto;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;

import java.util.List;
import java.util.Optional;

public interface DetalleCotizacionService {

    List<DetalleCotizacionResponseDto> findAll();

    Optional<DetalleCotizacionResponseDto> findById(Integer id);

    List<DetalleCotizacionResponseDto> findByCotizacionId(Integer cotizacionId);

    DetalleCotizacionResponseDto create(DetalleCotizacionRequestDto dto, Integer cotizacionId);

    DetalleCotizacionResponseDto patch(Integer id, DetalleCotizacionRequestDto dto);

    void delete(Integer id);
}