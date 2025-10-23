package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.service.ViajeroService;
import com.everywhere.backend.exceptions.ResourceNotFoundException; 
import com.everywhere.backend.mapper.ViajeroMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
 
import java.util.List; 
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViajeroServiceImpl implements ViajeroService {

    private final ViajeroRepository viajeroRepository;
    private final ViajeroMapper viajeroMapper;

    @Override
    public List<ViajeroResponseDTO> findAll() {
        return viajeroRepository.findAll().stream().map(viajeroMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public ViajeroResponseDTO findById(Integer id) {
        Viajero viajero = viajeroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con ID: " + id));
        return viajeroMapper.toResponseDTO(viajero);
    }

    @Override
    public List<ViajeroResponseDTO> findByNacionalidad(String nacionalidad) {
        List<Viajero> viajeros = viajeroRepository.findByNacionalidadIgnoreAccents(nacionalidad);
        if (viajeros.isEmpty()) throw new ResourceNotFoundException("No se encontraron viajeros con nacionalidad: " + nacionalidad);
        return viajeros.stream().map(viajeroMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<ViajeroResponseDTO> findByResidencia(String residencia) {
        List<Viajero> viajeros = viajeroRepository.findByResidenciaIgnoreAccents(residencia);
        if (viajeros.isEmpty()) throw new ResourceNotFoundException("No se encontraron viajeros con residencia: " + residencia);
        return viajeros.stream().map(viajeroMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public ViajeroResponseDTO save(ViajeroRequestDTO viajeroRequestDTO) {
        Viajero viajero = viajeroMapper.toEntity(viajeroRequestDTO);
        viajero = viajeroRepository.save(viajero);
        return viajeroMapper.toResponseDTO(viajero);
    }

    @Override
    public ViajeroResponseDTO patch(Integer id, ViajeroRequestDTO viajeroRequestDTO) {
        Viajero existingViajero = viajeroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con ID: " + id));

        viajeroMapper.updateEntityFromDTO(viajeroRequestDTO, existingViajero);
        existingViajero = viajeroRepository.save(existingViajero);
        return viajeroMapper.toResponseDTO(existingViajero);
    }

    @Override
    public void deleteById(Integer id) {
        if (!viajeroRepository.existsById(id)) throw new ResourceNotFoundException("Viajero no encontrado con ID: " + id);
        viajeroRepository.deleteById(id);
    }
}