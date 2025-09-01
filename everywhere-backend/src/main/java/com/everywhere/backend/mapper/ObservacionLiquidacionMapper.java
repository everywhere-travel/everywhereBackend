package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObeservacionLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.ObservacionLiquidacion;
import com.everywhere.backend.model.entity.Liquidacion;
import org.springframework.stereotype.Component;

@Component
public class ObservacionLiquidacionMapper {

    private final LiquidacionMapper liquidacionMapper;

    public ObservacionLiquidacionMapper(LiquidacionMapper liquidacionMapper) {
        this.liquidacionMapper = liquidacionMapper;
    }

    public ObservacionLiquidacion toEntity(ObservacionLiquidacionRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        ObservacionLiquidacion entity = new ObservacionLiquidacion();
        entity.setDescripcion(dto.getDescripcion());
        entity.setValor(dto.getValor());
        entity.setDocumento(dto.getDocumento());
        entity.setNumeroDocumento(dto.getNumeroDocumento());

        if (dto.getLiquidacionId() != null) {
            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setId(dto.getLiquidacionId());
            entity.setLiquidacion(liquidacion);
        }

        return entity;
    }

    public ObeservacionLiquidacionResponseDTO toResponseDTO(ObservacionLiquidacion entity) {
        if (entity == null) {
            return null;
        }

        ObeservacionLiquidacionResponseDTO dto = new ObeservacionLiquidacionResponseDTO();
        dto.setId(entity.getId());
        dto.setDescripcion(entity.getDescripcion());
        dto.setValor(entity.getValor());
        dto.setDocumento(entity.getDocumento());
        dto.setNumeroDocumento(entity.getNumeroDocumento());
        dto.setCreado(entity.getCreado());
        dto.setActualizado(entity.getActualizado());

        if (entity.getLiquidacion() != null) {
            dto.setLiquidacion(liquidacionMapper.toResponseDTO(entity.getLiquidacion()));
        }

        return dto;
    }

    public void updateEntityFromDTO(ObservacionLiquidacionRequestDTO dto, ObservacionLiquidacion entity) {
        if (dto == null || entity == null) {
            return;
        }

        // Solo actualizar campos que se envían (no son null)
        if (dto.getDescripcion() != null) {
            entity.setDescripcion(dto.getDescripcion());
        }
        if (dto.getValor() != null) {
            entity.setValor(dto.getValor());
        }
        if (dto.getDocumento() != null) {
            entity.setDocumento(dto.getDocumento());
        }
        if (dto.getNumeroDocumento() != null) {
            entity.setNumeroDocumento(dto.getNumeroDocumento());
        }

    }
}
