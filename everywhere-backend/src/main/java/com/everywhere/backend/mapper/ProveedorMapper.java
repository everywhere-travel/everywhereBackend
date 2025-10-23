package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ProveedorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorResponseDTO;
import com.everywhere.backend.model.entity.Proveedor;

public class ProveedorMapper {


    public static Proveedor toEntity(ProveedorRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        return proveedor;
    }

    public static ProveedorResponseDTO toResponse(Proveedor proveedor) {
        if (proveedor == null) {
            return null;
        }
        ProveedorResponseDTO dto = new ProveedorResponseDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setCreado(proveedor.getCreado());
        dto.setActualizado(proveedor.getActualizado());
        return dto;
    }
}
