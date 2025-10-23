package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.entity.PersonaNatural;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonaNaturalMapper {

    private final ModelMapper modelMapper;
    private final PersonaMapper personaMapper;

    public PersonaNaturalResponseDTO toResponseDTO(PersonaNatural personaNatural) {
        PersonaNaturalResponseDTO personaNaturalResponseDTO = modelMapper.map(personaNatural, PersonaNaturalResponseDTO.class);
        if (personaNatural.getPersonas() != null) {
            personaNaturalResponseDTO.setPersona(personaMapper.toResponseDTO(personaNatural.getPersonas()));
        }
        return personaNaturalResponseDTO;
    }

    public PersonaNatural toEntity(PersonaNaturalRequestDTO personaNaturalRequestDTOdto) {
        return modelMapper.map(personaNaturalRequestDTOdto, PersonaNatural.class);
    }

    public void updateEntityFromDTO(PersonaNaturalRequestDTO personaNaturalRequestDTO, PersonaNatural personaNatural) {
        modelMapper.getConfiguration().setSkipNullEnabled(true); // Configurar ModelMapper para saltar campos null 
        modelMapper.map(personaNaturalRequestDTO, personaNatural); // Mapeo automático
        
        if (personaNaturalRequestDTO.getPersona() != null && personaNatural.getPersonas() != null) // Actualizar persona base si existe
            personaMapper.updateEntityFromDTO(personaNaturalRequestDTO.getPersona(), personaNatural.getPersonas());
            
        // Nota: El categoriaPersonaId se maneja automáticamente por ModelMapper
        // ya que PersonaNatural tiene el campo categoriaPersona y el DTO tiene categoriaPersonaId
        // ModelMapper automáticamente mapea por ID cuando encuentra la relación
    }
}