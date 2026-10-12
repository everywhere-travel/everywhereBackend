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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaResponseDTO> findAll() {
        return personaRepository.findAll().stream().map(personaMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaResponseDTO findById(Integer id) {
        Personas persona = personaRepository.findByIdWithTelefonos(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));
        return personaMapper.toResponseDTO(persona);
    }


    @Override
    @Transactional(readOnly = true)
    public List<PersonaResponseDTO> findByEmail(String email) {
        List<Personas> personas = personaRepository.findByEmailContainingIgnoreCase(email);
        return personas.stream().map(personaMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonaTablaDTO> findPersonasPage(String searchTerm, String typeFilter, Pageable pageable) {
        String encryptedSearch = null;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            encryptedSearch = com.everywhere.backend.util.crypto.DniEncryptionUtil.encrypt(searchTerm.trim());
        }
        Page<PersonaTablaDTO> rawPage = personaRepository.findPersonasPage(searchTerm, encryptedSearch, typeFilter, pageable);
        return rawPage.map(PersonaTablaDTOImpl::fromProjection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonaResponseDTO> findByTelefono(String telefono) {
        List<Personas> personas = personaRepository.findByTelefonoContainingIgnoreCase(telefono);
        return personas.stream().map(personaMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public PersonaResponseDTO save(PersonaRequestDTO personaRequestDTO) {
        Personas persona = personaMapper.toEntity(personaRequestDTO);
        return personaMapper.toResponseDTO(personaRepository.save(persona));
    }

    @Override
    @Transactional
    public PersonaResponseDTO patch(Integer id, PersonaRequestDTO personaRequestDTO) {
        if (!personaRepository.existsById(id))
            throw new ResourceNotFoundException("Persona no encontrada con ID: " + id);

        Personas existingPersona = personaRepository.findById(id).get();
        personaMapper.updateEntityFromDTO(personaRequestDTO, existingPersona); 
        return personaMapper.toResponseDTO(personaRepository.save(existingPersona));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!personaRepository.existsById(id))
            throw new ResourceNotFoundException("Persona no encontrada con ID: " + id);
        personaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public java.util.Map<String, Long> getPersonaStats() {
        long totalNaturales = personaNaturalRepository.count();
        long totalJuridicas = personaJuridicaRepository.count();
        return java.util.Map.of(
            "totalNaturales", totalNaturales,
            "totalJuridicas", totalJuridicas
        );
    }

    @Override
    @Transactional
    public PersonaDisplayDto getOrCreateClientesVarios() {
        return personaNaturalRepository.findByDocumentoIgnoreCase("00000000")
                .map(pn -> new PersonaDisplayDto(
                        pn.getPersonas().getId(),
                        "natural",
                        "00000000",
                        "CLIENTES VARIOS (TRANSÚNTE)"
                ))
                .orElseGet(() -> {
                    Personas persona = new Personas();
                    persona.setEmail("clientesvarios@everywheretravel.com");
                    persona.setDireccion("Sin dirección (Clientes Varios)");
                    persona.setObservacion("Cliente genérico del sistema para cotizaciones y operaciones rápidas sin almacenamiento de datos personales (Ley N° 29733).");
                    Personas savedPersona = personaRepository.save(persona);

                    com.everywhere.backend.model.entity.PersonaNatural pn = new com.everywhere.backend.model.entity.PersonaNatural();
                    pn.setPersonas(savedPersona);
                    pn.setDocumento("00000000");
                    pn.setNombres("CLIENTES");
                    pn.setApellidosPaterno("VARIOS");
                    pn.setApellidosMaterno("(TRANSÚNTE)");
                    pn.setSexo("M");
                    personaNaturalRepository.save(pn);

                    return new PersonaDisplayDto(
                            savedPersona.getId(),
                            "natural",
                            "00000000",
                            "CLIENTES VARIOS (TRANSÚNTE)"
                    );
                });
    }
}