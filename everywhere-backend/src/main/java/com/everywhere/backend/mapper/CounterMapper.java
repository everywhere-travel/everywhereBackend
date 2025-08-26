package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.CounterRequestDto;
import com.everywhere.backend.model.dto.CounterResponseDto;
import com.everywhere.backend.model.entity.Counter;

public class CounterMapper {

    public static Counter toEntity(CounterRequestDto dto){
        Counter counter = new Counter();
        counter.setNombre(dto.getNombre());
        counter.setEstado(Boolean.TRUE);
        return counter;
    }

    public static CounterResponseDto toResponse(Counter entity){
        CounterResponseDto dto = new CounterResponseDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setEstado(entity.getEstado());
        dto.setCodigo(entity.getCodigo());
        return dto;
    }
}
