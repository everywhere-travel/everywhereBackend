package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.EstadoCotizacionRequestDTO;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDTO;
import com.everywhere.backend.model.entity.EstadoCotizacion;

public class EstadoCotizacionMapper {

    public static EstadoCotizacion toEntity(EstadoCotizacionRequestDTO dto) {
        EstadoCotizacion estadoCotizacion = new EstadoCotizacion();
        estadoCotizacion.setDescripcion(dto.getDescripcion());
        return estadoCotizacion;
    }

    public static EstadoCotizacionResponseDTO toResponse(EstadoCotizacion entity) {
        EstadoCotizacionResponseDTO dto = new EstadoCotizacionResponseDTO();
        dto.setId(entity.getId());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }
}
