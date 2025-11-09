package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.*;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.service.PersonaService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.PersonaMapper;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List; 
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "personas")
@Transactional(readOnly = true)
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final PersonaMapper personaMapper;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;

    @Override
    @Cacheable
    public List<PersonaResponseDTO> findAll() {
        return personaRepository.findAll().stream().map(personaMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "personaById", key = "#id")
    public PersonaResponseDTO findById(Integer id) {
        Personas persona = personaRepository.findByIdWithTelefonos(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + id));
        return personaMapper.toResponseDTO(persona);
    }


    @Override
    @Cacheable(value = "personasByEmail", key = "#email")
    public List<PersonaResponseDTO> findByEmail(String email) {
        List<Personas> personas = personaRepository.findByEmailContainingIgnoreCase(email);
        return personas.stream().map(personaMapper::toResponseDTO).toList();
    }

    @Override
    @Cacheable(value = "personasByTelefono", key = "#telefono")
    public List<PersonaResponseDTO> findByTelefono(String telefono) {
        List<Personas> personas = personaRepository.findByTelefonoContainingIgnoreCase(telefono);
        return personas.stream().map(personaMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    @CachePut(value = "personaById", key = "#result.id")
    @CacheEvict(
        value = { 
            "personas", "personasByEmail", "personasByTelefono", "personaDisplay",
            "cotizaciones", "cotizacionesSinLiquidacion",
            "documentosCobranza", "documentoCobranzaByCotizacionId",
            "personasNaturales", "personasJuridicas"
        },
        allEntries = true
    )
    public PersonaResponseDTO save(PersonaRequestDTO personaRequestDTO) {
        Personas persona = personaMapper.toEntity(personaRequestDTO);
        return personaMapper.toResponseDTO(personaRepository.save(persona));
    }

    @Override
    @Transactional
    @CachePut(value = "personaById", key = "#id")
    @CacheEvict(
        value = { 
            "personas", "personasByEmail", "personasByTelefono", "personaDisplay", 
            "correosPersona", "correoPersonaById", "correosPersonaByPersonaId",
            "telefonosPersona", "telefonoPersonaById", "telefonosPersonaByPersonaId", 
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion", 
            "documentosCobranza", "documentoCobranzaById", "documentoCobranzaByNumero", "documentoCobranzaByCotizacionId",
            "personasNaturales", "personaNaturalById",
            "personasJuridicas", "personaJuridicaById",
            "naturalJuridico", "naturalJuridicoById", "naturalJuridicoByPersonaNaturalId", "naturalJuridicoByPersonaJuridicaId",
            "categoriasPersona", "categoriaPersonaById", "categoriasPersonaByNombre", "personasPorCategoria", "categoriaDePersona",
            "detallesDocumento", "detalleDocumentoById", "detallesDocumentoByDocumentoId", "detallesDocumentoByNumero", 
            "detallesDocumentoByPersonaNaturalId", "detallesDocumentoByPersonaId",
            "viajeros", "viajeroById", "viajerosByNacionalidad", "viajerosByResidencia",
            "viajerosFrecuentes", "viajeroFrecuenteById", "viajerosFrecuentesByViajeroId"
        },
        allEntries = true
    )
    public PersonaResponseDTO patch(Integer id, PersonaRequestDTO personaRequestDTO) {
        if (!personaRepository.existsById(id))
            throw new ResourceNotFoundException("Persona no encontrada con ID: " + id);

        Personas existingPersona = personaRepository.findById(id).get();
        personaMapper.updateEntityFromDTO(personaRequestDTO, existingPersona); 
        return personaMapper.toResponseDTO(personaRepository.save(existingPersona));
    }

    @Override
    @Transactional
    @CacheEvict(
        value = { 
            "personas", "personaById", "personasByEmail", "personasByTelefono", "personaDisplay", 
            "correosPersona", "correoPersonaById", "correosPersonaByPersonaId",
            "telefonosPersona", "telefonoPersonaById", "telefonosPersonaByPersonaId", 
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion", 
            "documentosCobranza", "documentoCobranzaById", "documentoCobranzaByNumero", "documentoCobranzaByCotizacionId",
            "personasNaturales", "personaNaturalById",
            "personasJuridicas", "personaJuridicaById",
            "naturalJuridico", "naturalJuridicoById", "naturalJuridicoByPersonaNaturalId", "naturalJuridicoByPersonaJuridicaId",
            "categoriasPersona", "categoriaPersonaById", "categoriasPersonaByNombre", "personasPorCategoria", "categoriaDePersona",
            "detallesDocumento", "detalleDocumentoById", "detallesDocumentoByDocumentoId", "detallesDocumentoByNumero", 
            "detallesDocumentoByPersonaNaturalId", "detallesDocumentoByPersonaId",
            "viajeros", "viajeroById", "viajerosByNacionalidad", "viajerosByResidencia",
            "viajerosFrecuentes", "viajeroFrecuenteById", "viajerosFrecuentesByViajeroId"
        },
        allEntries = true
    )
    public void deleteById(Integer id) {
        if (!personaRepository.existsById(id))
            throw new ResourceNotFoundException("Persona no encontrada con ID: " + id);
        personaRepository.deleteById(id);
    }

    @Override
    @Cacheable(value = "personaDisplay", key = "#id")
    public PersonaDisplayDto findPersonaNaturalOrJuridicaById(Integer id) {
    personaRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID " + id));

    return personaNaturalRepository.findByPersonasId(id)
        .map(personaMapper::toDisplayDTO)
        .or(() -> personaJuridicaRepository.findByPersonasId(id).map(personaMapper::toDisplayDTO))
        .orElseThrow(() -> new ResourceNotFoundException(
            "No se encontró información adicional (natural o jurídica) para la persona con ID: " + id));
    }
}