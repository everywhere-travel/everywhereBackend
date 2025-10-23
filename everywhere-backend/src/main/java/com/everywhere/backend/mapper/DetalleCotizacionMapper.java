package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleCotizacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDTO;
import com.everywhere.backend.model.entity.DetalleCotizacion;
import com.everywhere.backend.repository.CategoriaRepository;

public class DetalleCotizacionMapper {

    public static DetalleCotizacionResponseDTO toResponse(DetalleCotizacion entity) {
        if (entity == null) {
            return null;
        }

    DetalleCotizacionResponseDTO dto = new DetalleCotizacionResponseDTO();
    dto.setId(entity.getId());
    dto.setCantidad(entity.getCantidad());
    dto.setUnidad(entity.getUnidad());
    dto.setDescripcion(entity.getDescripcion());
    dto.setPrecioHistorico(entity.getPrecioHistorico());
    dto.setSeleccionado(entity.getSeleccionado());
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

    public static DetalleCotizacion toEntity(DetalleCotizacionRequestDTO dto, com.everywhere.backend.repository.CategoriaRepository categoriaRepository) {
        if (dto == null) {
            return null;
        }

        DetalleCotizacion entity = new DetalleCotizacion();
        entity.setCantidad(dto.getCantidad());
        entity.setUnidad(dto.getUnidad());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCategoria(
            categoriaRepository.findById(dto.getCategoria()).orElse(null)
        );
        entity.setComision(dto.getComision());
        entity.setPrecioHistorico(dto.getPrecioHistorico());
        entity.setSeleccionado(dto.getSeleccionado());
        return entity;
    }

    public static void updateEntityFromRequest(
            DetalleCotizacion entity,
            DetalleCotizacionRequestDTO dto,
            CategoriaRepository categoriaRepository
    ) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setCantidad(dto.getCantidad());
        entity.setUnidad(dto.getUnidad());
        entity.setDescripcion(dto.getDescripcion());
        entity.setCategoria(categoriaRepository.findById(dto.getCategoria()).orElse(null));
        entity.setComision(dto.getComision());
        entity.setPrecioHistorico(dto.getPrecioHistorico());
        entity.setSeleccionado(dto.getSeleccionado());
    }
}
