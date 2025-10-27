package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObeservacionLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.ObservacionLiquidacion;
import com.everywhere.backend.model.entity.Liquidacion;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservacionLiquidacionMapper {

    private final ModelMapper modelMapper;
    private final LiquidacionMapper liquidacionMapper;

    @PostConstruct
    public void configureMapping() {
        modelMapper.typeMap(ObservacionLiquidacionRequestDTO.class, ObservacionLiquidacion.class)
                .addMappings(mapper -> {
                    mapper.skip(ObservacionLiquidacion::setId);
                    mapper.skip(ObservacionLiquidacion::setLiquidacion);
                    mapper.skip(ObservacionLiquidacion::setCreado);
                    mapper.skip(ObservacionLiquidacion::setActualizado);
                });
    }

    public ObservacionLiquidacion toEntity(ObservacionLiquidacionRequestDTO requestDTO) {
        if (requestDTO == null) {
            return null;
        }

        ObservacionLiquidacion entity = modelMapper.map(requestDTO, ObservacionLiquidacion.class);

        if (requestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setId(requestDTO.getLiquidacionId());
            entity.setLiquidacion(liquidacion);
        }

        return entity;
    }

    public ObeservacionLiquidacionResponseDTO toResponseDTO(ObservacionLiquidacion observacionLiquidacion) {
        if (observacionLiquidacion == null) {
            return null;
        }

        ObeservacionLiquidacionResponseDTO obeservacionLiquidacionResponseDTO =
                modelMapper.map(observacionLiquidacion, ObeservacionLiquidacionResponseDTO.class);

        if (observacionLiquidacion.getLiquidacion() != null) {
            obeservacionLiquidacionResponseDTO.setLiquidacion(liquidacionMapper.toResponseDTO(observacionLiquidacion.getLiquidacion()));
        }

        return obeservacionLiquidacionResponseDTO;
    }

    public void updateEntityFromDTO(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO, ObservacionLiquidacion entity) {
        if (observacionLiquidacionRequestDTO == null || entity == null) {
            return;
        }
        modelMapper.map(observacionLiquidacionRequestDTO, entity);
        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setId(observacionLiquidacionRequestDTO.getLiquidacionId());
            entity.setLiquidacion(liquidacion);
        }
    }
}
