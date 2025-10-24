package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.EstadoCotizacionRequestDto;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDto;

import java.util.List;
import java.util.Optional;

public interface EstadoCotizacionService {

    EstadoCotizacionResponseDto create(EstadoCotizacionRequestDto dto);

    EstadoCotizacionResponseDto update(Integer id, EstadoCotizacionRequestDto dto);

    Optional<EstadoCotizacionResponseDto> getById(Integer id);

    List<EstadoCotizacionResponseDto> getAll();

    void delete(Integer id);
}

