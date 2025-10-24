package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.CounterRequestDTO;
import com.everywhere.backend.model.dto.CounterResponseDTO;
import com.everywhere.backend.model.entity.Counter;
import java.time.LocalDateTime;
public class CounterMapper {

    public static Counter toEntity(CounterRequestDTO dto){
        Counter counter = new Counter();
        counter.setNombre(dto.getNombre());
        counter.setEstado(Boolean.TRUE);
        return counter;
    }

    public static Counter toEntityForUpdate(CounterRequestDTO dto, Counter existingEntity) {
        existingEntity.setNombre(dto.getNombre());
        existingEntity.setFechaActualizacion(LocalDateTime.now());
        return existingEntity;
    }

    public static CounterResponseDTO toResponse(Counter entity){
        CounterResponseDTO dto = new CounterResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEstado(entity.getEstado());
        dto.setCodigo(entity.getCodigo());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setFechaActualizacion(entity.getFechaActualizacion());
        return dto;
    }
}
