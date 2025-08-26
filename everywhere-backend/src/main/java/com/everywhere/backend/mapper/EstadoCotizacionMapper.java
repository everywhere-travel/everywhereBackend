package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.EstadoCotizacionRequestDto;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDto;
import com.everywhere.backend.model.entity.EstadoCotizacion;

public class EstadoCotizacionMapper {

    public static EstadoCotizacion toEntity(EstadoCotizacionRequestDto dto) {
        EstadoCotizacion estadoCotizacion = new EstadoCotizacion();
        estadoCotizacion.setDescripcion(dto.getDescripcion());
        return estadoCotizacion;
    }

    public static EstadoCotizacionResponseDto toResponse(EstadoCotizacion entity) {
        EstadoCotizacionResponseDto dto = new EstadoCotizacionResponseDto();
        dto.setId(entity.getId());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }
}
