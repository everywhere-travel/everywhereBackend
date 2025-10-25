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

@Component
@RequiredArgsConstructor
public class PersonaMapper {

    private final ModelMapper modelMapper;

    public PersonaResponseDTO toResponseDTO(Personas persona) {
        return modelMapper.map(persona, PersonaResponseDTO.class);
    }

    public Personas toEntity(PersonaRequestDTO personaRequestDTO) {
        return modelMapper.map(personaRequestDTO, Personas.class);
    }

    public void updateEntityFromDTO(PersonaRequestDTO personaRequestDTO, Personas personas) {
        modelMapper.map(personaRequestDTO, personas); // Mapeo automático
    }

    public PersonaDisplayDto toDisplayDTO(PersonaNatural personaNatural) {
        String nombreCompleto = personaNatural.getNombres() + " " + personaNatural.getApellidosPaterno() + " " + personaNatural.getApellidosMaterno();
        return new PersonaDisplayDto(
                personaNatural.getId(),
                "NATURAL",
                String.valueOf(personaNatural.getDocumento()),
                nombreCompleto
        );
    }

    public PersonaDisplayDto toDisplayDTO(PersonaJuridica personaJuridica) {
        return new PersonaDisplayDto(
                personaJuridica.getId(),
                "JURIDICA",
                String.valueOf(personaJuridica.getRuc()),
                personaJuridica.getRazonSocial()
        );
    }
}
