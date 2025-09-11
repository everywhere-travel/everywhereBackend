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
    dto.setComision(entity.getComision());

    // Solo IDs de relaciones
    dto.setCotizacion(entity.getCotizacion());
    dto.setProducto(entity.getProducto());
    dto.setProveedor(entity.getProveedor());
    dto.setCategoria(entity.getCategoria());

    return dto;
    }

    public static DetalleCotizacion toEntity(DetalleCotizacionRequestDto dto, com.everywhere.backend.repository.CategoriaRepository categoriaRepository) {
        if (dto == null) {
            return null;
        }

        DetalleCotizacion entity = new DetalleCotizacion();
        entity.setCantidad(dto.getCantidad());
        entity.setUnidad(dto.getUnidad());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCategoria(categoriaRepository.getOne(dto.getCategoria()));
        entity.setComision(dto.getComision());
        entity.setPrecioHistorico(dto.getPrecioHistorico());
        return entity;
    }

    public static void updateEntityFromRequest(DetalleCotizacion entity, DetalleCotizacionRequestDto dto, com.everywhere.backend.repository.CategoriaRepository categoriaRepository) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setCantidad(dto.getCantidad());
        entity.setUnidad(dto.getUnidad());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCategoria(categoriaRepository.getOne(dto.getCategoria()));
        entity.setComision(dto.getComision());
        entity.setPrecioHistorico(dto.getPrecioHistorico());
    }
}
