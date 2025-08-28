package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.PersonaNaturalRequestDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.service.PersonaNaturalService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.mapper.PersonaNaturalMapper;
import com.everywhere.backend.mapper.PersonaMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        Optional<PersonaNatural> persona = personaNaturalRepository.findByDocumentoIgnoreCase(documento);
        if (persona.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró persona natural con documento: " + documento);
        }
        return List.of(personaNaturalMapper.toResponseDTO(persona.get()));
    }

    @Override
    public List<PersonaNaturalResponseDTO> findByNombres(String nombres) {
        List<PersonaNatural> personas = personaNaturalRepository.findByNombresIgnoreAccents(nombres);
        if (personas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron personas naturales con nombres: " + nombres);
        }
        return personas.stream()
                .map(personaNaturalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonaNaturalResponseDTO> findByApellidos(String apellidos) {
        List<PersonaNatural> personas = personaNaturalRepository.findByApellidosIgnoreAccents(apellidos);
        if (personas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron personas naturales con apellidos: " + apellidos);
        }
        return personas.stream()
                .map(personaNaturalMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PersonaNaturalResponseDTO save(PersonaNaturalRequestDTO personaNaturalRequestDTO) {
        // Validar que no exista ya una persona con el mismo documento (solo si el documento no es null/vacío)
        if (personaNaturalRequestDTO.getDocumento() != null && !personaNaturalRequestDTO.getDocumento().trim().isEmpty()) {
            Optional<PersonaNatural> existingPersona = personaNaturalRepository.findByDocumentoIgnoreCase(personaNaturalRequestDTO.getDocumento());
            if (existingPersona.isPresent()) {
                throw new BadRequestException("Ya existe una persona natural con el documento: " + personaNaturalRequestDTO.getDocumento());
            }
        }

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

        // Validar que no exista otra persona con el mismo documento (solo si el documento no es null/vacío)
        if (personaNaturalRequestDTO.getDocumento() != null && !personaNaturalRequestDTO.getDocumento().trim().isEmpty()) {
            Optional<PersonaNatural> personaConMismoDocumento = personaNaturalRepository.findByDocumentoIgnoreCase(personaNaturalRequestDTO.getDocumento());
            if (personaConMismoDocumento.isPresent() && !personaConMismoDocumento.get().getId().equals(id)) {
                throw new BadRequestException("Ya existe otra persona natural con el documento: " + personaNaturalRequestDTO.getDocumento());
            }
        }

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
