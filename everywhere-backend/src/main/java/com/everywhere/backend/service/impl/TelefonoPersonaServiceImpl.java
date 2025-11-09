package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.TelefonoPersonaMapper;
import com.everywhere.backend.model.dto.TelefonoPersonaRequestDTO;
import com.everywhere.backend.model.dto.TelefonoPersonaResponseDTO;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.entity.TelefonoPersona;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.repository.TelefonoPersonaRepository;
import com.everywhere.backend.service.TelefonoPersonaService;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "telefonosPersona")
public class TelefonoPersonaServiceImpl implements TelefonoPersonaService {

    private final TelefonoPersonaRepository telefonoPersonaRepository;
    private final PersonaRepository personaRepository;
    private final TelefonoPersonaMapper telefonoPersonaMapper;

    @Override
    @Cacheable
    public List<TelefonoPersonaResponseDTO> findAll() {
        return telefonoPersonaRepository.findAll()
                .stream().map(telefonoPersonaMapper::toResponseDTO).toList();
    }

    @Override
    @Cacheable(value = "telefonoPersonaById", key = "{#telefonoId, #personaId}")
    public Optional<TelefonoPersonaResponseDTO> findById(Integer telefonoId, Integer personaId) {
        return telefonoPersonaRepository.findByIdAndPersonaId(telefonoId, personaId)
                .map(telefonoPersonaMapper::toResponseDTO);
    }

    @Override
    @Cacheable(value = "telefonosPersonaByPersonaId", key = "#personaId")
    public List<TelefonoPersonaResponseDTO> findByPersonaId(Integer personaId) {
        return telefonoPersonaRepository.findByPersonaId(personaId)
                .stream()
                .map(telefonoPersonaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    @CachePut(value = "telefonoPersonaById", key = "{#result.id, #personaId}")
    @CacheEvict(
        value = { 
            "telefonosPersona", "telefonosPersonaByPersonaId", 
            "personas", "personaById", "personasByEmail", "personasByTelefono", "personaDisplay",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion",
            "personasNaturales", "personaNaturalById",
            "categoriasPersona", "categoriaPersonaById", "categoriasPersonaByNombre", "personasPorCategoria", "categoriaDePersona",
            "detallesDocumento", "detalleDocumentoById", "detallesDocumentoByDocumentoId", "detallesDocumentoByNumero", 
            "detallesDocumentoByPersonaNaturalId", "detallesDocumentoByPersonaId",
            "viajeros", "viajeroById", "viajerosByNacionalidad", "viajerosByResidencia",
            "viajerosFrecuentes", "viajeroFrecuenteById", "viajerosFrecuentesByViajeroId"
        },
        allEntries = true
    )
    public TelefonoPersonaResponseDTO save(TelefonoPersonaRequestDTO telefonoPersonaRequestDTO, Integer personaId) {
        Personas persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + personaId));

        TelefonoPersona telefono = telefonoPersonaMapper.toEntity(telefonoPersonaRequestDTO);
        telefono.setPersona(persona);
        return telefonoPersonaMapper.toResponseDTO(telefonoPersonaRepository.save(telefono));
    }

    @Override
    @Transactional
    @CachePut(value = "telefonoPersonaById", key = "{#telefonoId, #personaId}")
    @CacheEvict(
        value = { 
            "telefonosPersona", "telefonosPersonaByPersonaId", 
            "personas", "personaById", "personasByEmail", "personasByTelefono", "personaDisplay",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion",
            "personasNaturales", "personaNaturalById",
            "categoriasPersona", "categoriaPersonaById", "categoriasPersonaByNombre", "personasPorCategoria", "categoriaDePersona",
            "detallesDocumento", "detalleDocumentoById", "detallesDocumentoByDocumentoId", "detallesDocumentoByNumero", 
            "detallesDocumentoByPersonaNaturalId", "detallesDocumentoByPersonaId",
            "viajeros", "viajeroById", "viajerosByNacionalidad", "viajerosByResidencia",
            "viajerosFrecuentes", "viajeroFrecuenteById", "viajerosFrecuentesByViajeroId"
        },
        allEntries = true
    )
    public TelefonoPersonaResponseDTO update(Integer personaId, TelefonoPersonaRequestDTO telefonoPersonaRequestDTO, Integer telefonoId) {
        TelefonoPersona telefono = telefonoPersonaRepository.findByIdAndPersonaId(telefonoId, personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Teléfono no encontrado con ID: " + telefonoId + " para la persona con ID: "+ personaId));

        telefonoPersonaMapper.updateEntityFromDTO(telefonoPersonaRequestDTO, telefono);
        return telefonoPersonaMapper.toResponseDTO(telefonoPersonaRepository.save(telefono));
    }


    @Override
    @Transactional
    @CacheEvict(
        value = {
            "telefonosPersona", "telefonoPersonaById", "telefonosPersonaByPersonaId",
            "personas", "personaById", "personasByEmail", "personasByTelefono", "personaDisplay",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion",
            "personasNaturales", "personaNaturalById",
            "categoriasPersona", "categoriaPersonaById", "categoriasPersonaByNombre", "personasPorCategoria", "categoriaDePersona",
            "detallesDocumento", "detalleDocumentoById", "detallesDocumentoByDocumentoId", "detallesDocumentoByNumero", 
            "detallesDocumentoByPersonaNaturalId", "detallesDocumentoByPersonaId",
            "viajeros", "viajeroById", "viajerosByNacionalidad", "viajerosByResidencia",
            "viajerosFrecuentes", "viajeroFrecuenteById", "viajerosFrecuentesByViajeroId"
        }, 
        allEntries = true
    )
    public void deleteById(Integer telefonoId, Integer personaId) {
        if (!telefonoPersonaRepository.existsByIdAndPersonaId(telefonoId, personaId))
            throw new ResourceNotFoundException("Teléfono no encontrado con ID: " + telefonoId + " para la persona con ID: " + personaId);
        telefonoPersonaRepository.deleteById(telefonoId);
    }
}
