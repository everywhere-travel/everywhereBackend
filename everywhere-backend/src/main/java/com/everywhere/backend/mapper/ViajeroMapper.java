package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.entity.Viajero;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViajeroMapper {

    private final ModelMapper modelMapper;
    private final PersonaNaturalMapper personaNaturalMapper;

    public ViajeroResponseDTO toResponseDTO(Viajero viajero) {
        ViajeroResponseDTO dto = modelMapper.map(viajero, ViajeroResponseDTO.class);
        
        // Mapear PersonaNatural si existe
        if (viajero.getPersonaNatural() != null) {
            PersonaNaturalResponseDTO personaNaturalDto = personaNaturalMapper.toResponseDTO(viajero.getPersonaNatural());
            dto.setPersonaNatural(personaNaturalDto);
        }
        
        return dto;
    }

    public Viajero toEntity(ViajeroRequestDTO viajeroRequestDTO) { 
        return modelMapper.map(viajeroRequestDTO, Viajero.class);
    }

    public void updateEntityFromDTO(ViajeroRequestDTO viajeroRequestDTO, Viajero viajero) {
        modelMapper.map(viajeroRequestDTO, viajero); 
    }
}
