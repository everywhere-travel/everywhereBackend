package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.OperadorRequestDTO;
import com.everywhere.backend.model.dto.OperadorResponseDTO;
import com.everywhere.backend.model.entity.Operador;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OperadorMapper {

    private final ModelMapper modelMapper;

    public OperadorResponseDTO toResponseDTO(Operador operador) {
        return modelMapper.map(operador, OperadorResponseDTO.class);
    }

    public Operador toEntity(OperadorRequestDTO dto) {
        return modelMapper.map(dto, Operador.class);
    }

    public void updateEntityFromDTO(OperadorRequestDTO dto, Operador operador) {
        modelMapper.map(dto, operador);
    }
}