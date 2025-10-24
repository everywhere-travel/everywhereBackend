package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ViajeroFrecuenteRequestDTO;
import com.everywhere.backend.model.dto.ViajeroFrecuenteResponseDTO;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.model.entity.ViajeroFrecuente;

import java.time.LocalDateTime;

public class ViajeroFrecuenteMapper {

    // Convierte RequestDto a entidad (recibiendo el Viajero desde el service)
    public static ViajeroFrecuente toEntity(ViajeroFrecuenteRequestDTO dto, Viajero viajero) {
        ViajeroFrecuente entity = new ViajeroFrecuente();
        entity.setAreolinea(dto.getAreolinea());
        entity.setCodigo(dto.getCodigo());
        entity.setViajero(viajero);
        entity.setCreado(LocalDateTime.now());
        entity.setActualizado(LocalDateTime.now());
        return entity;
    }

    // Convierte entidad a ResponseDto
    public static ViajeroFrecuenteResponseDTO toResponse(ViajeroFrecuente entity) {
        ViajeroFrecuenteResponseDTO dto = new ViajeroFrecuenteResponseDTO();
        dto.setId(entity.getId());
        dto.setAreolinea(entity.getAreolinea());
        dto.setCodigo(entity.getCodigo());
        dto.setViajero(entity.getViajero());
        dto.setCreado(entity.getCreado());
        dto.setActualizado(entity.getActualizado());
        return dto;
    }
}
