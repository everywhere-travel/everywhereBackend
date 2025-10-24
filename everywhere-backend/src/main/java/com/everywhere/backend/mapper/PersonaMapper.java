package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.PersonaRequestDTO;
import com.everywhere.backend.model.dto.PersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaDisplayDTO;
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
        modelMapper.getConfiguration().setSkipNullEnabled(true); // Configurar ModelMapper para saltar campos null
        modelMapper.map(personaRequestDTO, personas); // Mapeo automático
    }

    public PersonaDisplayDTO toDisplayDTO(PersonaNatural personaNatural) {
        String nombreCompleto = personaNatural.getNombres() + " " + personaNatural.getApellidosPaterno() + " " + personaNatural.getApellidosMaterno();
        return new PersonaDisplayDTO(
                personaNatural.getId(),
                "NATURAL",
                String.valueOf(personaNatural.getDocumento()),
                nombreCompleto
        );
    }

    public PersonaDisplayDTO toDisplayDTO(PersonaJuridica personaJuridica) {
        return new PersonaDisplayDTO(
                personaJuridica.getId(),
                "JURIDICA",
                String.valueOf(personaJuridica.getRuc()),
                personaJuridica.getRazonSocial()
        );
    }
}
