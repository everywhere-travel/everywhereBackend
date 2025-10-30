package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.CorreoPersonaRequestDTO;
import com.everywhere.backend.model.dto.CorreoPersonaResponseDTO;
import com.everywhere.backend.model.entity.CorreoPersona;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CorreoPersonaMapper {

    @Autowired
    private ModelMapper modelMapper;

    public CorreoPersona toEntity(CorreoPersonaRequestDTO correoPersona) {
        return modelMapper.map(correoPersona, CorreoPersona.class);
    }

    public CorreoPersonaResponseDTO toResponseDTO(CorreoPersona correoPersona) {
        return modelMapper.map(correoPersona, CorreoPersonaResponseDTO.class);
    }

    public void updateEntityFromDTO(CorreoPersona correoPersonaSource, CorreoPersona correoPersonaTarget) {
        modelMapper.map(correoPersonaSource, correoPersonaTarget);
    }
}
