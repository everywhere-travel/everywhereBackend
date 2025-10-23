package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.OperadorRequestDTO;
import com.everywhere.backend.model.dto.OperadorResponseDTO;
import com.everywhere.backend.model.entity.Operador;

public class OperadorMapper {

    public static Operador toEntity(OperadorRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Operador operador = new Operador();
        operador.setNombre(dto.getNombre());
        return operador;
    }

    public static OperadorResponseDTO toResponse(Operador operador) {
        if (operador == null) {
            return null;
        }
        OperadorResponseDTO dto = new OperadorResponseDTO();
        dto.setId(operador.getId());
        dto.setNombre(operador.getNombre());
        dto.setCreado(operador.getCreado());
        dto.setActualizado(operador.getActualizado());
        return dto;
    }

}
