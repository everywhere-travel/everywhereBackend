package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.DetalleLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.DetalleLiquidacion;

import jakarta.annotation.PostConstruct;

import org.modelmapper.ModelMapper; 
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DetalleLiquidacionMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void init() {
        modelMapper.typeMap(DetalleLiquidacionRequestDTO.class, DetalleLiquidacion.class)
                .addMappings(mapper -> mapper.skip(DetalleLiquidacion::setId));
    }

    public DetalleLiquidacionResponseDTO toResponseDTO(DetalleLiquidacion detalleLiquidacion) {
        DetalleLiquidacionResponseDTO detalleLiquidacionResponseDTO = modelMapper.map(detalleLiquidacion, DetalleLiquidacionResponseDTO.class);
        return detalleLiquidacionResponseDTO;
    }

    public DetalleLiquidacion toEntity(DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacion detalleLiquidacion = modelMapper.map(detalleLiquidacionRequestDTO, DetalleLiquidacion.class);
        return detalleLiquidacion;
    }

    public void updateEntityFromDTO(DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO, DetalleLiquidacion detalleLiquidacion) {
        modelMapper.map(detalleLiquidacionRequestDTO, detalleLiquidacion);
    }
}