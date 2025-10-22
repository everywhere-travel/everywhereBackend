package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.BadRequestException;
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
import java.util.Optional;
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
        return personaJuridicaRepository.findAll().stream().map(personaJuridicaMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public PersonaJuridicaResponseDTO findById(Integer id) {
        PersonaJuridica personaJuridica = personaJuridicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + id));
        return personaJuridicaMapper.toResponseDTO(personaJuridica);
    }

    @Override
    public List<PersonaJuridicaResponseDTO> findByRuc(String ruc) {
        Optional<PersonaJuridica> persona = personaJuridicaRepository.findByRucIgnoreCase(ruc);
        if (persona.isEmpty())
            throw new ResourceNotFoundException("No se encontró persona jurídica con RUC: " + ruc);
        return List.of(personaJuridicaMapper.toResponseDTO(persona.get()));
    }

    @Override
    public List<PersonaJuridicaResponseDTO> findByRazonSocial(String razonSocial) {
        List<PersonaJuridica> personas = personaJuridicaRepository.findByRazonSocialIgnoreAccents(razonSocial);
        if (personas.isEmpty())
            throw new ResourceNotFoundException("No se encontraron personas jurídicas con razón social: " + razonSocial);    
        return personas.stream().map(personaJuridicaMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public PersonaJuridicaResponseDTO save(PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        // Validar que no exista ya una persona con el mismo RUC
        if (personaJuridicaRequestDTO.getRuc() != null && !personaJuridicaRequestDTO.getRuc().trim().isEmpty()) {
            Optional<PersonaJuridica> existingPersona = personaJuridicaRepository.findByRucIgnoreCase(personaJuridicaRequestDTO.getRuc().trim());
            if (existingPersona.isPresent())
                throw new BadRequestException("Ya existe una persona jurídica con el RUC: " + personaJuridicaRequestDTO.getRuc());
        }

        // Crear la persona base
        Personas persona = null;
        if (personaJuridicaRequestDTO.getPersona() != null) 
            persona = personaMapper.toEntity(personaJuridicaRequestDTO.getPersona());
        else 
            persona = new Personas();
        
        persona.setCreado(LocalDateTime.now());
        persona = personaRepository.save(persona);

        // Crear la persona jurídica
        PersonaJuridica personaJuridica = personaJuridicaMapper.toEntity(personaJuridicaRequestDTO);
        personaJuridica.setPersonas(persona);
        personaJuridica = personaJuridicaRepository.save(personaJuridica);

        return personaJuridicaMapper.toResponseDTO(personaJuridica);
    }

    @Override
    public PersonaJuridicaResponseDTO patch(Integer id, PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        PersonaJuridica existingPersonaJuridica = personaJuridicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + id));

        // Validar que no exista otra persona con el mismo RUC
        if (personaJuridicaRequestDTO.getRuc() != null && !personaJuridicaRequestDTO.getRuc().trim().isEmpty()) {
            Optional<PersonaJuridica> personaConMismoRuc = personaJuridicaRepository.findByRucIgnoreCase(personaJuridicaRequestDTO.getRuc().trim());
            if (personaConMismoRuc.isPresent() && !personaConMismoRuc.get().getId().equals(id)) 
                throw new BadRequestException("Ya existe otra persona jurídica con el RUC: " + personaJuridicaRequestDTO.getRuc());
        }

        personaJuridicaMapper.updateEntityFromDTO(personaJuridicaRequestDTO, existingPersonaJuridica);
        existingPersonaJuridica = personaJuridicaRepository.save(existingPersonaJuridica);
        return personaJuridicaMapper.toResponseDTO(existingPersonaJuridica);
    }

    @Override
    public void deleteById(Integer id) {
        if (!personaJuridicaRepository.existsById(id))
            throw new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + id);
        personaJuridicaRepository.deleteById(id);
    }
}
