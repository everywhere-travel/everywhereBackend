package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.PersonaJuridicaRequestDTO;
import com.everywhere.backend.model.dto.PersonaJuridicaResponseDTO;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.service.PersonaJuridicaService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.PersonaJuridicaMapper;
import com.everywhere.backend.mapper.PersonaMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaJuridicaServiceImpl implements PersonaJuridicaService {

    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final PersonaRepository personaRepository;
    private final PersonaJuridicaMapper personaJuridicaMapper;
    private final PersonaMapper personaMapper;

    @Override
    public List<PersonaJuridicaResponseDTO> findAll() {
        return personaJuridicaRepository.findAll().stream()
                .map(personaJuridicaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonaJuridicaResponseDTO findById(Integer id) {
        PersonaJuridica personaJuridica = personaJuridicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + id));
        return personaJuridicaMapper.toResponseDTO(personaJuridica);
    }

    @Override
    public List<PersonaJuridicaResponseDTO> findByRuc(String ruc) {
        List<PersonaJuridica> personas = personaJuridicaRepository.findByRucContainingIgnoreCase(ruc);
        return personas.stream()
                .map(personaJuridicaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonaJuridicaResponseDTO> findByRazonSocial(String razonSocial) {
        List<PersonaJuridica> personas = personaJuridicaRepository.findByRazonSocialContainingIgnoreCase(razonSocial);
        return personas.stream()
                .map(personaJuridicaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonaJuridicaResponseDTO save(PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        // Crear la persona base
        Personas persona = null;
        if (personaJuridicaRequestDTO.getPersona() != null) {
            persona = personaMapper.toEntity(personaJuridicaRequestDTO.getPersona());
        } else {
            persona = new Personas();
        }
        persona.setCreado(LocalDateTime.now());
        persona = personaRepository.save(persona);

        // Crear la persona jurídica
        PersonaJuridica personaJuridica = personaJuridicaMapper.toEntity(personaJuridicaRequestDTO);
        personaJuridica.setPersonas(persona);
        personaJuridica.setCreado(LocalDateTime.now());
        personaJuridica = personaJuridicaRepository.save(personaJuridica);

        return personaJuridicaMapper.toResponseDTO(personaJuridica);
    }

    @Override
    public PersonaJuridicaResponseDTO update(Integer id, PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        PersonaJuridica existingPersonaJuridica = personaJuridicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + id));

        personaJuridicaMapper.updateEntityFromDTO(personaJuridicaRequestDTO, existingPersonaJuridica);
        existingPersonaJuridica = personaJuridicaRepository.save(existingPersonaJuridica);
        return personaJuridicaMapper.toResponseDTO(existingPersonaJuridica);
    }

    @Override
    public void deleteById(Integer id) {
        if (!personaJuridicaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + id);
        }
        personaJuridicaRepository.deleteById(id);
    }
}
