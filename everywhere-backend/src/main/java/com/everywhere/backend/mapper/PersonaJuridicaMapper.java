package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.PersonaJuridicaRequestDTO;
import com.everywhere.backend.model.dto.PersonaJuridicaResponseDTO;
import com.everywhere.backend.model.entity.PersonaJuridica;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PersonaJuridicaMapper {

    private final ModelMapper modelMapper;
    private final PersonaMapper personaMapper;

    public PersonaJuridicaResponseDTO toResponseDTO(PersonaJuridica personaJuridica) {
        PersonaJuridicaResponseDTO dto = modelMapper.map(personaJuridica, PersonaJuridicaResponseDTO.class);
        if (personaJuridica.getPersonas() != null) {
            dto.setPersona(personaMapper.toResponseDTO(personaJuridica.getPersonas()));
        }
        return dto;
    }

    public PersonaJuridica toEntity(PersonaJuridicaRequestDTO dto) {
        return modelMapper.map(dto, PersonaJuridica.class);
    }

    public void updateEntityFromDTO(PersonaJuridicaRequestDTO dto, PersonaJuridica entity) {
        // Actualizar campos específicos de PersonaJuridica
        if (dto.getRuc() != null) {
            entity.setRuc(dto.getRuc());
        }
        if (dto.getRazonSocial() != null) {
            entity.setRazonSocial(dto.getRazonSocial());
        }

        // Actualizar persona base si existe
        if (dto.getPersona() != null && entity.getPersonas() != null) {
            personaMapper.updateEntityFromDTO(dto.getPersona(), entity.getPersonas());
        }

        entity.setActualizado(LocalDateTime.now());
    }
}
