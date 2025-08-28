package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.PersonaRequestDTO;
import com.everywhere.backend.model.dto.PersonaResponseDTO;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.service.PersonaService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.PersonaMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;

    @Override
    public List<PersonaResponseDTO> findAll() {
        return personaRepository.findAll().stream()
                .map(personaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonaResponseDTO findById(Integer id) {
        Personas persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));
        return personaMapper.toResponseDTO(persona);
    }

    @Override
    public List<PersonaResponseDTO> findByEmail(String email) {
        List<Personas> personas = personaRepository.findByEmailContainingIgnoreCase(email);
        return personas.stream()
                .map(personaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonaResponseDTO> findByTelefono(String telefono) {
        List<Personas> personas = personaRepository.findByTelefonoContainingIgnoreCase(telefono);
        return personas.stream()
                .map(personaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonaResponseDTO save(PersonaRequestDTO personaRequestDTO) {
        Personas persona = personaMapper.toEntity(personaRequestDTO);
        persona.setCreado(LocalDateTime.now());
        persona = personaRepository.save(persona);
        return personaMapper.toResponseDTO(persona);
    }

    @Override
    public PersonaResponseDTO update(Integer id, PersonaRequestDTO personaRequestDTO) {
        Personas existingPersona = personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));

        personaMapper.updateEntityFromDTO(personaRequestDTO, existingPersona);
        existingPersona = personaRepository.save(existingPersona);
        return personaMapper.toResponseDTO(existingPersona);
    }

    @Override
    public void deleteById(Integer id) {
        if (!personaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Persona no encontrada con ID: " + id);
        }
        personaRepository.deleteById(id);
    }
}
