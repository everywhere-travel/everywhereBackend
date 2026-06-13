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

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor; 
import java.util.List;
import java.util.Optional; 

@Service
@RequiredArgsConstructor
public class PersonaJuridicaServiceImpl implements PersonaJuridicaService {

    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final PersonaRepository personaRepository;
    private final PersonaJuridicaMapper personaJuridicaMapper;
    private final PersonaMapper personaMapper;

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<PersonaJuridicaResponseDTO> findAll() {
        // 1. Obtener solo los IDs de los últimos 100 registros (consulta rápida sin joins)
        List<Integer> ids = personaJuridicaRepository.findTop100ByOrderByIdDesc()
                .stream()
                .map(PersonaJuridica::getId)
                .toList();

        if (ids.isEmpty()) {
            return List.of();
        }

        // 2. Traer las entidades completas usando los IDs con JOIN FETCH
        return personaJuridicaRepository.findConDetalles(ids)
                .stream()
                .map(personaJuridicaMapper::toResponseDTO) // Usamos tu mapper original
                .toList();
    }
    @Override
    public PersonaJuridicaResponseDTO findById(Integer id) {
        PersonaJuridica personaJuridica = personaJuridicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + id));
        return personaJuridicaMapper.toResponseDTO(personaJuridica);
    }

    @Override
    public List<PersonaJuridicaResponseDTO> findByRuc(String ruc) {
        Optional<PersonaJuridica> personaJuridOptional = personaJuridicaRepository.findByRucIgnoreCase(ruc);
        if (personaJuridOptional.isEmpty())
            throw new ResourceNotFoundException("No se encontró persona jurídica con RUC: " + ruc);
        return List.of(personaJuridicaMapper.toResponseDTO(personaJuridOptional.get()));
    }

    @Override
    public List<PersonaJuridicaResponseDTO> findByRazonSocial(String razonSocial) {
        List<PersonaJuridica> personaJuridicaList = personaJuridicaRepository.findByRazonSocialIgnoreAccents(razonSocial);
        if (personaJuridicaList.isEmpty())
            throw new ResourceNotFoundException("No se encontraron personas jurídicas con razón social: " + razonSocial);
        return personaJuridicaList.stream().map(personaJuridicaMapper::toResponseDTO).toList();
    }

    @Override
    public PersonaJuridicaResponseDTO save(PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        // Validar que no exista ya una persona con el mismo RUC
        if (personaJuridicaRequestDTO.getRuc() != null && !personaJuridicaRequestDTO.getRuc().trim().isEmpty()) { 
            if (personaJuridicaRepository.findByRucIgnoreCase(personaJuridicaRequestDTO.getRuc().trim()).isPresent())
                throw new DataIntegrityViolationException("Ya existe una persona jurídica con el RUC: " + personaJuridicaRequestDTO.getRuc());
        }

        // Crear la persona base
        Personas persona = (personaJuridicaRequestDTO.getPersona() != null) 
            ? personaMapper.toEntity(personaJuridicaRequestDTO.getPersona())
            : new Personas(); 

        // Crear la persona jurídica
        PersonaJuridica personaJuridica = personaJuridicaMapper.toEntity(personaJuridicaRequestDTO);
        personaJuridica.setPersonas(personaRepository.save(persona)); 

        return personaJuridicaMapper.toResponseDTO(personaJuridicaRepository.save(personaJuridica));
    }

    @Override
    public PersonaJuridicaResponseDTO patch(Integer id, PersonaJuridicaRequestDTO personaJuridicaRequestDTO) {
        // 🚀 OPTIMIZACIÓN 1: Validar existencia ANTES de buscar el objeto
        if (!personaJuridicaRepository.existsById(id))
            throw new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + id);

        // 🚀 OPTIMIZACIÓN 2: Si viene RUC, validar duplicado ANTES de buscar el objeto completo
        if (personaJuridicaRequestDTO.getRuc() != null && !personaJuridicaRequestDTO.getRuc().trim().isEmpty()) { 
            String newRuc = personaJuridicaRequestDTO.getRuc().trim();
            if (personaJuridicaRepository.findByRucIgnoreCaseAndIdNot(newRuc, id).isPresent()) {
                throw new DataIntegrityViolationException("Ya existe otra persona jurídica con el RUC: " + personaJuridicaRequestDTO.getRuc());
            }
        }

        // Solo ahora buscar el objeto para hacer el update
        PersonaJuridica existingPersonaJuridica = personaJuridicaRepository.findById(id).get();
        personaJuridicaMapper.updateEntityFromDTO(personaJuridicaRequestDTO, existingPersonaJuridica); 
        return personaJuridicaMapper.toResponseDTO(personaJuridicaRepository.save(existingPersonaJuridica));
    }

    @Override
    public void deleteById(Integer id) {
        if (!personaJuridicaRepository.existsById(id))
            throw new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + id);
        personaJuridicaRepository.deleteById(id);
    }
}