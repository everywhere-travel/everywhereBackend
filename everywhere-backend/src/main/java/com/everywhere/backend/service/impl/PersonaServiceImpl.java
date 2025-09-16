package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.service.PersonaService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.PersonaMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;
    private final PersonaNaturalRepository personaNaturalRepository;
    private  final PersonaJuridicaRepository personaJuridicaRepository;

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

    @Override
    public PersonaDisplayDto findPersonaNaturalOrJuridicaById(Integer id) {
        Personas base = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID " + id));

        // Verificamos si existe como NATURAL
        Optional<PersonaNatural> naturalOpt = personaNaturalRepository.findByPersonasId(id);
        if (naturalOpt.isPresent()) {
            return personaMapper.toDisplayDTO(naturalOpt.get());
        }

        // Verificamos si existe como JURIDICA
        Optional<PersonaJuridica> juridicaOpt = personaJuridicaRepository.findByPersonasId(id);
        if (juridicaOpt.isPresent()) {
            return personaMapper.toDisplayDTO(juridicaOpt.get());
        }

        // Si existe en tabla base, pero no está ni en natural ni en jurídica
        return new PersonaDisplayDto(base.getId(), "GENERICA", null, "Persona sin tipo definido");
    }


}

