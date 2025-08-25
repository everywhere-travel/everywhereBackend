package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ProveedorRequestDto;
import com.everywhere.backend.model.dto.ProveedorResponseDto;
import com.everywhere.backend.model.entity.Proveedor;

public class ProveedorMapper {


    public static Proveedor toEntity(ProveedorRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        return proveedor;
    }

    public static ProveedorResponseDto toResponse(Proveedor proveedor) {
        if (proveedor == null) {
            return null;
        }
        ProveedorResponseDto dto = new ProveedorResponseDto();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setCreado(proveedor.getCreado());
        dto.setActualizado(proveedor.getActualizado());
        return dto;
    }
}
