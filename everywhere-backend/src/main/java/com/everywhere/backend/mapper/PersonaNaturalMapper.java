package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.entity.PersonaNatural;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PersonaNaturalMapper {

    private final ModelMapper modelMapper;
    private final PersonaMapper personaMapper;

    public PersonaNaturalResponseDTO toResponseDTO(PersonaNatural personaNatural) {
        PersonaNaturalResponseDTO dto = modelMapper.map(personaNatural, PersonaNaturalResponseDTO.class);
        if (personaNatural.getPersonas() != null) {
            dto.setPersona(personaMapper.toResponseDTO(personaNatural.getPersonas()));
        }
        return dto;
    }

    public PersonaNatural toEntity(PersonaNaturalRequestDTO dto) {
        return modelMapper.map(dto, PersonaNatural.class);
    }

    public void updateEntityFromDTO(PersonaNaturalRequestDTO dto, PersonaNatural entity) {
        // Actualizar campos específicos de PersonaNatural
        if (dto.getDocumento() != null) {
            entity.setDocumento(dto.getDocumento());
        }
        if (dto.getNombres() != null) {
            entity.setNombres(dto.getNombres());
        }
        if (dto.getApellidos() != null) {
            entity.setApellidos(dto.getApellidos());
        }
        if (dto.getCliente() != null) {
            entity.setCliente(dto.getCliente());
        }
        if (dto.getCategoria() != null) {
            entity.setCategoria(dto.getCategoria());
        }

        // Actualizar persona base si existe
        if (dto.getPersona() != null && entity.getPersonas() != null) {
            personaMapper.updateEntityFromDTO(dto.getPersona(), entity.getPersonas());
        }

        entity.setActualizado(LocalDateTime.now());
    }
}
