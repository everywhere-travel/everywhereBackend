package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.dto.PersonaTablaDTO;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.service.PersonaService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.PersonaMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List; 
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;

    @Override
    public List<PersonaResponseDTO> findAll() {
        return personaRepository.findAll().stream().map(personaMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public PersonaResponseDTO findById(Integer id) {
        Personas persona = personaRepository.findByIdWithTelefonos(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));
        return personaMapper.toResponseDTO(persona);
    }


    @Override
    public List<PersonaResponseDTO> findByEmail(String email) {
        List<Personas> personas = personaRepository.findByEmailContainingIgnoreCase(email);
        return personas.stream().map(personaMapper::toResponseDTO).toList();
    }

    @Override
    public Page<PersonaTablaDTO> findPersonasPage(String searchTerm, String typeFilter, Pageable pageable) {
        return personaRepository.findPersonasPage(searchTerm, typeFilter, pageable);
    }

    @Override
    public List<PersonaResponseDTO> findByTelefono(String telefono) {
        List<Personas> personas = personaRepository.findByTelefonoContainingIgnoreCase(telefono);
        return personas.stream().map(personaMapper::toResponseDTO).toList();
    }

    @Override
    public PersonaResponseDTO save(PersonaRequestDTO personaRequestDTO) {
        Personas persona = personaMapper.toEntity(personaRequestDTO);
        return personaMapper.toResponseDTO(personaRepository.save(persona));
    }

    @Override
    public PersonaResponseDTO patch(Integer id, PersonaRequestDTO personaRequestDTO) {
        if (!personaRepository.existsById(id))
            throw new ResourceNotFoundException("Persona no encontrada con ID: " + id);

        Personas existingPersona = personaRepository.findById(id).get();
        personaMapper.updateEntityFromDTO(personaRequestDTO, existingPersona); 
        return personaMapper.toResponseDTO(personaRepository.save(existingPersona));
    }

    @Override
    public void deleteById(Integer id) {
        if (!personaRepository.existsById(id))
            throw new ResourceNotFoundException("Persona no encontrada con ID: " + id);
        personaRepository.deleteById(id);
    }

    @Override
    public PersonaDisplayDto findPersonaNaturalOrJuridicaById(Integer id) {
        personaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID " + id));

        return personaNaturalRepository.findByPersonasId(id)
            .map(personaMapper::toDisplayDTO)
            .or(() -> personaJuridicaRepository.findByPersonasId(id).map(personaMapper::toDisplayDTO))
            .orElseThrow(() -> new ResourceNotFoundException(
                "No se encontró información adicional (natural o jurídica) para la persona con ID: " + id));
    }

    @Override
    public java.util.Map<String, Long> getPersonaStats() {
        long totalNaturales = personaNaturalRepository.count();
        long totalJuridicas = personaJuridicaRepository.count();
        return java.util.Map.of(
            "totalNaturales", totalNaturales,
            "totalJuridicas", totalJuridicas
        );
    }
}