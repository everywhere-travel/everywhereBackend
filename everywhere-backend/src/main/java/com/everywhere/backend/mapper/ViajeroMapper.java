package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.model.entity.Viajero;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ViajeroMapper {

    private final ModelMapper modelMapper;
    private final PersonaMapper personaMapper;

    public ViajeroResponseDTO toResponseDTO(Viajero viajero) {
        ViajeroResponseDTO dto = modelMapper.map(viajero, ViajeroResponseDTO.class);
        if (viajero.getPersonas() != null) {
            dto.setPersona(personaMapper.toResponseDTO(viajero.getPersonas()));
        }
        return dto;
    }

    public Viajero toEntity(ViajeroRequestDTO dto) {
        Viajero entity = modelMapper.map(dto, Viajero.class);

        if (dto.getPersona() != null) {
            entity.setPersonas(personaMapper.toEntity(dto.getPersona()));
        }

        entity.setCreado(LocalDateTime.now());
        entity.setActualizado(LocalDateTime.now());
        return entity;
    }

    public void updateEntityFromDTO(ViajeroRequestDTO dto, Viajero entity) {

        if (dto.getNombres() != null) {
            entity.setNombres(dto.getNombres());
        }
        if (dto.getApellidoPaterno() != null) {
            entity.setApellidoPaterno(dto.getApellidoPaterno());
        }
        if (dto.getApellidoMaterno() != null) {
            entity.setApellidoMaterno(dto.getApellidoMaterno());
        }
        if (dto.getFechaNacimiento() != null) {
            entity.setFechaNacimiento(dto.getFechaNacimiento());
        }
        if (dto.getNacionalidad() != null) {
            entity.setNacionalidad(dto.getNacionalidad());
        }
        if (dto.getResidencia() != null) {
            entity.setResidencia(dto.getResidencia());
        }

        if (dto.getPersona() != null && entity.getPersonas() != null) {
            personaMapper.updateEntityFromDTO(dto.getPersona(), entity.getPersonas());
        } else if (dto.getPersona() != null) {
            entity.setPersonas(personaMapper.toEntity(dto.getPersona()));
        }

        entity.setActualizado(LocalDateTime.now());
    }
}
