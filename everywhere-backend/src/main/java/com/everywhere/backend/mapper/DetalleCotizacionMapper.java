package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleCotizacionRequestDto;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.model.entity.DetalleCotizacion;

public class DetalleCotizacionMapper {

    public static DetalleCotizacionResponseDto toResponse(DetalleCotizacion entity) {
        if (entity == null) {
            return null;
        }

        DetalleCotizacionResponseDto dto = new DetalleCotizacionResponseDto();
        dto.setId(entity.getId());
        dto.setCantidad(entity.getCantidad());
        dto.setUnidad(entity.getUnidad());
        dto.setDescripcion(entity.getDescripcion());
        dto.setPrecioHistorico(entity.getPrecioHistorico());
        dto.setCreado(entity.getCreado());
        dto.setActualizado(entity.getActualizado());

        // Relaciones completas
        dto.setCotizacion(entity.getCotizacion());
        dto.setProducto(entity.getProducto());
        dto.setProveedor(entity.getProveedor());

        return dto;
    }

    public static DetalleCotizacion toEntity(DetalleCotizacionRequestDto dto) {
        if (dto == null) {
            return null;
        }

        DetalleCotizacion entity = new DetalleCotizacion();
        entity.setCantidad(dto.getCantidad());
        entity.setUnidad(dto.getUnidad());
        entity.setDescripcion(dto.getDescripcion());

        return entity;
    }

    public static void updateEntityFromRequest(DetalleCotizacion entity, DetalleCotizacionRequestDto dto) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setCantidad(dto.getCantidad());
        entity.setUnidad(dto.getUnidad());
        entity.setDescripcion(dto.getDescripcion());
    }
}
