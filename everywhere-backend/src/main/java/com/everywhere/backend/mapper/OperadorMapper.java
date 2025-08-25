package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.OperadorRequestDto;
import com.everywhere.backend.model.dto.OperadorResponseDto;
import com.everywhere.backend.model.entity.Operador;

public class OperadorMapper {

    public static Operador toEntity(OperadorRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Operador operador = new Operador();
        operador.setNombre(dto.getNombre());
        return operador;
    }

    public static OperadorResponseDto toResponse(Operador operador) {
        if (operador == null) {
            return null;
        }
        OperadorResponseDto dto = new OperadorResponseDto();
        dto.setId(operador.getId());
        dto.setNombre(operador.getNombre());
        dto.setCreado(operador.getCreado());
        dto.setActualizado(operador.getActualizado());
        return dto;
    }

}
