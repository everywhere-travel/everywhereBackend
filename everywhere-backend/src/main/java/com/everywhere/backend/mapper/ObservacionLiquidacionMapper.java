package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObeservacionLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.ObservacionLiquidacion;
import com.everywhere.backend.model.entity.Liquidacion;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservacionLiquidacionMapper {

    private final ModelMapper modelMapper;
    private final LiquidacionMapper liquidacionMapper;

    public ObservacionLiquidacion toEntity(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) {
        if (observacionLiquidacionRequestDTO == null) {
            return null;
        }

        ObservacionLiquidacion entity = modelMapper.map(observacionLiquidacionRequestDTO, ObservacionLiquidacion.class);

        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setId(observacionLiquidacionRequestDTO.getLiquidacionId());
            entity.setLiquidacion(liquidacion);
        }
        return entity;
    }

    public ObeservacionLiquidacionResponseDTO toResponseDTO(ObservacionLiquidacion entity) {
        if (entity == null) {
            return null;
        }
        ObeservacionLiquidacionResponseDTO responseDTO = modelMapper.map(entity, ObeservacionLiquidacionResponseDTO.class);
        if (entity.getLiquidacion() != null) {
            responseDTO.setLiquidacion(liquidacionMapper.toResponseDTO(entity.getLiquidacion()));
        }
        return responseDTO;
    }

    public void updateEntityFromDTO(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO, ObservacionLiquidacion entity) {
        if (observacionLiquidacionRequestDTO == null || entity == null) {
            return;
        }
        if (observacionLiquidacionRequestDTO.getDescripcion() != null) {
            entity.setDescripcion(observacionLiquidacionRequestDTO.getDescripcion());
        }
        if (observacionLiquidacionRequestDTO.getValor() != null) {
            entity.setValor(observacionLiquidacionRequestDTO.getValor());
        }
        if (observacionLiquidacionRequestDTO.getDocumento() != null) {
            entity.setDocumento(observacionLiquidacionRequestDTO.getDocumento());
        }
        if (observacionLiquidacionRequestDTO.getNumeroDocumento() != null) {
            entity.setNumeroDocumento(observacionLiquidacionRequestDTO.getNumeroDocumento());
        }
        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setId(observacionLiquidacionRequestDTO.getLiquidacionId());
            entity.setLiquidacion(liquidacion);
        }
    }
}
