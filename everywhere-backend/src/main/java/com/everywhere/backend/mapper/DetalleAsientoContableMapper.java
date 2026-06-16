package com.everywhere.backend.mapper;

import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.DetalleAsientoContableResponseDTO;
import com.everywhere.backend.model.entity.DetalleAsientoContable;

@Component
public class DetalleAsientoContableMapper {

    public DetalleAsientoContableResponseDTO toResponse(
            DetalleAsientoContable entity) {

        DetalleAsientoContableResponseDTO dto =
                new DetalleAsientoContableResponseDTO();

        dto.setId(entity.getId());

        dto.setCuentaId(entity.getCuenta().getId());

        dto.setCuentaCodigo(entity.getCuenta().getCodigo());

        dto.setCuentaNombre(entity.getCuenta().getNombre());

        dto.setDebe(entity.getDebe());

        dto.setHaber(entity.getHaber());

        dto.setCreado(entity.getCreado());

        dto.setActualizado(entity.getActualizado());

        return dto;
    }
}