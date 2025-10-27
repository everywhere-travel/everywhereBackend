package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObservacionLiquidacionResponseDTO;
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
                .addMappings(mapper -> mapper.skip(ObservacionLiquidacion::setLiquidacion));
    }

    public ObservacionLiquidacion toEntity(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) {
        if (observacionLiquidacionRequestDTO == null) {
            return null;
        }

        ObservacionLiquidacion observacionLiquidacion =
                modelMapper.map(observacionLiquidacionRequestDTO, ObservacionLiquidacion.class);

        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setId(observacionLiquidacionRequestDTO.getLiquidacionId());
            observacionLiquidacion.setLiquidacion(liquidacion);
        }

        return observacionLiquidacion;
    }

    public ObservacionLiquidacionResponseDTO toResponseDTO(ObservacionLiquidacion observacionLiquidacion) {
        if (observacionLiquidacion == null) {
            return null;
        }

        ObservacionLiquidacionResponseDTO observacionLiquidacionResponseDTO =
                modelMapper.map(observacionLiquidacion, ObservacionLiquidacionResponseDTO.class);

        if (observacionLiquidacion.getLiquidacion() != null) {
            observacionLiquidacionResponseDTO.setLiquidacion(
                    liquidacionMapper.toResponseDTO(observacionLiquidacion.getLiquidacion()));
        }

        return observacionLiquidacionResponseDTO;
    }

    public void updateEntityFromDTO(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO,
                                    ObservacionLiquidacion observacionLiquidacion) {
        if (observacionLiquidacionRequestDTO == null || observacionLiquidacion == null) {
            return;
        }

        modelMapper.map(observacionLiquidacionRequestDTO, observacionLiquidacion);

        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = new Liquidacion();
            liquidacion.setId(observacionLiquidacionRequestDTO.getLiquidacionId());
            observacionLiquidacion.setLiquidacion(liquidacion);
        }
    }
}
