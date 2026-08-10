package com.everywhere.backend.mapper;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Viajero;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViajeroMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureMapping() {

        modelMapper.typeMap(Viajero.class, ViajeroResponseDTO.class)
                .addMappings(mapper -> mapper.skip(ViajeroResponseDTO::setPersonaNatural));
    }

    public ViajeroResponseDTO toResponseDTO(Viajero viajero) {
        ViajeroResponseDTO dto = modelMapper.map(viajero, ViajeroResponseDTO.class);
        dto.setPersonaNatural(toResumen(viajero.getPersonaNatural()));
        return dto;
    }

    public ViajeroResponseDTO toResponseDTO(Viajero viajero, PersonaNatural personaNaturalYaResuelto) {
        ViajeroResponseDTO dto = modelMapper.map(viajero, ViajeroResponseDTO.class);
        dto.setPersonaNatural(toResumen(personaNaturalYaResuelto));
        return dto;
    }

    private ViajeroResponseDTO.PersonaNaturalResumenDTO toResumen(PersonaNatural personaNatural) {
        if (personaNatural == null) {
            return null;
        }
        return ViajeroResponseDTO.PersonaNaturalResumenDTO.builder()
                .id(personaNatural.getId())
                .nombres(personaNatural.getNombres())
                .apellidosPaterno(personaNatural.getApellidosPaterno())
                .apellidosMaterno(personaNatural.getApellidosMaterno())
                .documento(personaNatural.getDocumento())
                .build();
    }

    public Viajero toEntity(ViajeroRequestDTO viajeroRequestDTO) {
        return modelMapper.map(viajeroRequestDTO, Viajero.class);
    }

    public void updateEntityFromDTO(ViajeroRequestDTO viajeroRequestDTO, Viajero viajero) {
        modelMapper.map(viajeroRequestDTO, viajero); 
    }
}
