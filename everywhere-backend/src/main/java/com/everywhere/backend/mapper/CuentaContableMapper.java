package com.everywhere.backend.mapper;

import org.springframework.stereotype.Component;

import com.everywhere.backend.model.dto.CuentaContableRequestDTO;
import com.everywhere.backend.model.dto.CuentaContableResponseDTO;
import com.everywhere.backend.model.entity.CuentaContable;

@Component
public class CuentaContableMapper {

    public CuentaContable toEntity(CuentaContableRequestDTO dto) {

        CuentaContable entity = new CuentaContable();

        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setTipo(dto.getTipo());
        entity.setActivo(dto.getActivo());

        return entity;
    }

    public CuentaContableResponseDTO toResponse(CuentaContable entity) {

        CuentaContableResponseDTO dto = new CuentaContableResponseDTO();

        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setTipo(entity.getTipo());
        dto.setActivo(entity.getActivo());
        dto.setCreado(entity.getCreado());
        dto.setActualizado(entity.getActualizado());

        return dto;
    }
}