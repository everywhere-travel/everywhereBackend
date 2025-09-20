package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.PersonaRequestDTO;
import com.everywhere.backend.model.dto.PersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaDisplayDto;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Personas;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PersonaMapper {

    private final ModelMapper modelMapper;

    public PersonaResponseDTO toResponseDTO(Personas persona) {
        return modelMapper.map(persona, PersonaResponseDTO.class);
    }

    public Personas toEntity(PersonaRequestDTO dto) {
        return modelMapper.map(dto, Personas.class);
    }

    public void updateEntityFromDTO(PersonaRequestDTO dto, Personas entity) {
        // Solo mapea campos no nulos
        if (dto.getEmail() != null) {
            entity.setEmail(dto.getEmail());
        }
        if (dto.getTelefono() != null) {
            entity.setTelefono(dto.getTelefono());
        }
        if (dto.getDireccion() != null) {
            entity.setDireccion(dto.getDireccion());
        }
        if (dto.getObservacion() != null) {
            entity.setObservacion(dto.getObservacion());
        }
        entity.setActualizado(LocalDateTime.now());
    }

    public PersonaDisplayDto toDisplayDTO(PersonaNatural natural) {
        String nombreCompleto = natural.getNombres() + " " + natural.getApellidos();
        return new PersonaDisplayDto(
                natural.getId(),
                "NATURAL",
                String.valueOf(natural.getDocumento()),
                nombreCompleto
        );
    }

    public PersonaDisplayDto toDisplayDTO(PersonaJuridica juridica) {
        return new PersonaDisplayDto(
                juridica.getId(),
                "JURIDICA",
                String.valueOf(juridica.getRuc()),
                juridica.getRazonSocial()
        );
    }
}
