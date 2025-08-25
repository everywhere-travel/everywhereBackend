package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.service.PersonaNaturalService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.PersonaNaturalMapper;
import com.everywhere.backend.mapper.PersonaMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaNaturalServiceImpl implements PersonaNaturalService {

    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaRepository personaRepository;
    private final PersonaNaturalMapper personaNaturalMapper;
    private final PersonaMapper personaMapper;

    @Override
    public List<PersonaNaturalResponseDTO> findAll() {
        return personaNaturalRepository.findAll().stream()
                .map(personaNaturalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonaNaturalResponseDTO findById(Integer id) {
        PersonaNatural personaNatural = personaNaturalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona natural no encontrada con ID: " + id));
        return personaNaturalMapper.toResponseDTO(personaNatural);
    }

    @Override
    public List<PersonaNaturalResponseDTO> findByDocumento(String documento) {
        List<PersonaNatural> personas = personaNaturalRepository.findByDocumentoContainingIgnoreCase(documento);
        return personas.stream()
                .map(personaNaturalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonaNaturalResponseDTO> findByNombres(String nombres) {
        List<PersonaNatural> personas = personaNaturalRepository.findByNombresContainingIgnoreCase(nombres);
        return personas.stream()
                .map(personaNaturalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonaNaturalResponseDTO> findByApellidos(String apellidos) {
        List<PersonaNatural> personas = personaNaturalRepository.findByApellidosContainingIgnoreCase(apellidos);
        return personas.stream()
                .map(personaNaturalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonaNaturalResponseDTO save(PersonaNaturalRequestDTO personaNaturalRequestDTO) {
        // Crear la persona base
        Personas persona = null;
        if (personaNaturalRequestDTO.getPersona() != null) {
            persona = personaMapper.toEntity(personaNaturalRequestDTO.getPersona());
        } else {
            persona = new Personas();
        }
        persona.setCreado(LocalDateTime.now());
        persona = personaRepository.save(persona);

        // Crear la persona natural
        PersonaNatural personaNatural = personaNaturalMapper.toEntity(personaNaturalRequestDTO);
        personaNatural.setPersonas(persona);
        personaNatural.setCreado(LocalDateTime.now());
        personaNatural = personaNaturalRepository.save(personaNatural);

        return personaNaturalMapper.toResponseDTO(personaNatural);
    }

    @Override
    public PersonaNaturalResponseDTO update(Integer id, PersonaNaturalRequestDTO personaNaturalRequestDTO) {
        PersonaNatural existingPersonaNatural = personaNaturalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona natural no encontrada con ID: " + id));

        personaNaturalMapper.updateEntityFromDTO(personaNaturalRequestDTO, existingPersonaNatural);
        existingPersonaNatural = personaNaturalRepository.save(existingPersonaNatural);
        return personaNaturalMapper.toResponseDTO(existingPersonaNatural);
    }

    @Override
    public void deleteById(Integer id) {
        if (!personaNaturalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Persona natural no encontrada con ID: " + id);
        }
        personaNaturalRepository.deleteById(id);
    }
}
