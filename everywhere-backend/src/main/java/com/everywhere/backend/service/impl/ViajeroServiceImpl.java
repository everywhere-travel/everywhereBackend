package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.service.ViajeroService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.mapper.ViajeroMapper;
import com.everywhere.backend.mapper.PersonaMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViajeroServiceImpl implements ViajeroService {

    private final ViajeroRepository viajeroRepository;
    private final PersonaRepository personaRepository;
    private final ViajeroMapper viajeroMapper;
    private final PersonaMapper personaMapper;

    @Override
    public List<ViajeroResponseDTO> findAll() {
        return viajeroRepository.findAll().stream()
                .map(viajeroMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ViajeroResponseDTO findById(Integer id) {
        Viajero viajero = viajeroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con ID: " + id));
        return viajeroMapper.toResponseDTO(viajero);
    }

    @Override
    public List<ViajeroResponseDTO> findByNombres(String nombres) {
        List<Viajero> viajeros = viajeroRepository.findByNombresIgnoreAccents(nombres);
        if (viajeros.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron viajeros con nombres: " + nombres);
        }
        return viajeros.stream()
                .map(viajeroMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ViajeroResponseDTO> findByNacionalidad(String nacionalidad) {
        List<Viajero> viajeros = viajeroRepository.findByNacionalidadIgnoreAccents(nacionalidad);
        if (viajeros.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron viajeros con nacionalidad: " + nacionalidad);
        }
        return viajeros.stream()
                .map(viajeroMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ViajeroResponseDTO> findByResidencia(String residencia) {
        List<Viajero> viajeros = viajeroRepository.findByResidenciaIgnoreAccents(residencia);
        if (viajeros.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron viajeros con residencia: " + residencia);
        }
        return viajeros.stream()
                .map(viajeroMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ViajeroResponseDTO save(ViajeroRequestDTO viajeroRequestDTO) {

        // Crear la persona base
        Personas persona = null;
        if (viajeroRequestDTO.getPersona() != null) {
            persona = personaMapper.toEntity(viajeroRequestDTO.getPersona());
        } else {
            persona = new Personas();
        }
        persona.setCreado(LocalDateTime.now());
        persona = personaRepository.save(persona);

        // Crear el viajero
        Viajero viajero = viajeroMapper.toEntity(viajeroRequestDTO);
        viajero.setPersonas(persona);
        viajero.setCreado(LocalDateTime.now());
        viajero = viajeroRepository.save(viajero);

        return viajeroMapper.toResponseDTO(viajero);
    }

    @Override
    public ViajeroResponseDTO update(Integer id, ViajeroRequestDTO viajeroRequestDTO) {
        Viajero existingViajero = viajeroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con ID: " + id));

        viajeroMapper.updateEntityFromDTO(viajeroRequestDTO, existingViajero);
        existingViajero = viajeroRepository.save(existingViajero);
        return viajeroMapper.toResponseDTO(existingViajero);
    }

    @Override
    public void deleteById(Integer id) {
        if (!viajeroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Viajero no encontrado con ID: " + id);
        }
        viajeroRepository.deleteById(id);
    }
}
