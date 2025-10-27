package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.TelefonoPersonaRequestDTO;
import com.everywhere.backend.model.dto.TelefonoPersonaResponseDTO;
import com.everywhere.backend.model.entity.TelefonoPersona;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TelefonoPersonaMapper {

    @Autowired
    private ModelMapper modelMapper;

    public TelefonoPersona toEntity(TelefonoPersonaRequestDTO dto) {
        return modelMapper.map(dto, TelefonoPersona.class);
    }

    public TelefonoPersonaResponseDTO toResponseDTO(TelefonoPersona entity) {
        return modelMapper.map(entity, TelefonoPersonaResponseDTO.class);
    }

    public void updateEntityFromDTO(TelefonoPersonaRequestDTO dto, TelefonoPersona entity) {
        modelMapper.map(dto, entity);
    }
}
