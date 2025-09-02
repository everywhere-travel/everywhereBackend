package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ViajeroFrecuenteRequestDto;
import com.everywhere.backend.model.dto.ViajeroFrecuenteResponseDto;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.model.entity.ViajeroFrecuente;

import java.time.LocalDateTime;

public class ViajeroFrecuenteMapper {

    // Convierte RequestDto a entidad (recibiendo el Viajero desde el service)
    public static ViajeroFrecuente toEntity(ViajeroFrecuenteRequestDto dto, Viajero viajero) {
        ViajeroFrecuente entity = new ViajeroFrecuente();
        entity.setAreolinea(dto.getAreolinea());
        entity.setCodigo(dto.getCodigo());
        entity.setViajero(viajero);
        entity.setCreado(LocalDateTime.now());
        entity.setActualizado(LocalDateTime.now());
        return entity;
    }

    // Convierte entidad a ResponseDto
    public static ViajeroFrecuenteResponseDto toResponse(ViajeroFrecuente entity) {
        ViajeroFrecuenteResponseDto dto = new ViajeroFrecuenteResponseDto();
        dto.setId(entity.getId());
        dto.setAreolinea(entity.getAreolinea());
        dto.setCodigo(entity.getCodigo());
        dto.setViajero(entity.getViajero());
        dto.setCreado(entity.getCreado());
        dto.setActualizado(entity.getActualizado());
        return dto;
    }
}
