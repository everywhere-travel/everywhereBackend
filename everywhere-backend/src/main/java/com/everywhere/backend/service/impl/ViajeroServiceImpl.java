package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.service.ViajeroService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ViajeroMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
 
import java.util.List; 

@Service
@RequiredArgsConstructor
public class ViajeroServiceImpl implements ViajeroService {

    private final ViajeroRepository viajeroRepository;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final ViajeroMapper viajeroMapper;

    @Override
    public List<ViajeroResponseDTO> findAll() {
        return mapToResponseList(viajeroRepository.findAll());
    }

    @Override
    public ViajeroResponseDTO findById(Integer id) {
        Viajero viajero = viajeroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con ID: " + id));
        return viajeroMapper.toResponseDTO(viajero);
    }

    @Override
    public List<ViajeroResponseDTO> findByNacionalidad(String nacionalidad) {
        return mapToResponseList(viajeroRepository.findByNacionalidadIgnoreAccents(nacionalidad));
    }

    @Override
    public List<ViajeroResponseDTO> findByResidencia(String residencia) {
        return mapToResponseList(viajeroRepository.findByResidenciaIgnoreAccents(residencia));
    }

    @Override
    public ViajeroResponseDTO save(ViajeroRequestDTO viajeroRequestDTO) {
        Viajero viajero = viajeroMapper.toEntity(viajeroRequestDTO);
        
        // Si viene personaNaturalId, buscar y asignar PersonaNatural
        if (viajeroRequestDTO.getPersonaNaturalId() != null) {
            PersonaNatural personaNatural = personaNaturalRepository.findById(viajeroRequestDTO.getPersonaNaturalId())
                    .orElseThrow(() -> new DataIntegrityViolationException("PersonaNatural no encontrada con ID: " + viajeroRequestDTO.getPersonaNaturalId()));
            viajero.setPersonaNatural(personaNatural);
            personaNatural.setViajero(viajero); // Asignar Viajero a PersonaNatural (lado propietario)
        }
        
        Viajero savedViajero = viajeroRepository.save(viajero);
        return viajeroMapper.toResponseDTO(savedViajero);
    }

    @Override
    public ViajeroResponseDTO patch(Integer id, ViajeroRequestDTO viajeroRequestDTO) {
        if (!viajeroRepository.existsById(id))
            throw new ResourceNotFoundException("Viajero no encontrado con ID: " + id);

        Viajero existingViajero = viajeroRepository.findById(id).get();
        viajeroMapper.updateEntityFromDTO(viajeroRequestDTO, existingViajero);        
        existingViajero = viajeroRepository.save(existingViajero);
        return viajeroMapper.toResponseDTO(existingViajero);
    }

    @Override
    public void deleteById(Integer id) {
        if (!viajeroRepository.existsById(id)) throw new ResourceNotFoundException("Viajero no encontrado con ID: " + id);
        viajeroRepository.deleteById(id);
    }

    private List<ViajeroResponseDTO> mapToResponseList(List<Viajero> viajeros) {
        return viajeros.stream().map(viajeroMapper::toResponseDTO).toList();
    }
}
