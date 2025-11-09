package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CorreoPersonaMapper;
import com.everywhere.backend.model.dto.CorreoPersonaRequestDTO;
import com.everywhere.backend.model.dto.CorreoPersonaResponseDTO;
import com.everywhere.backend.model.entity.CorreoPersona;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.repository.CorreoPersonaRepository;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.service.CorreoPersonaService;
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
@CacheConfig(cacheNames = "correosPersona")
@Transactional(readOnly = true)
public class CorreoPersonaServiceImpl implements CorreoPersonaService {

    private final CorreoPersonaRepository correoPersonaRepository;
    private final CorreoPersonaMapper correoPersonaMapper;
    private final PersonaRepository personaRepository;

    @Override
    @Cacheable
    public List<CorreoPersonaResponseDTO> findAll() {
        return correoPersonaRepository.findAll()
                .stream()
                .map(correoPersonaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Cacheable(value = "correoPersonaById", key = "#id")
    public Optional<CorreoPersonaResponseDTO> findById(Integer id) {
        return correoPersonaRepository.findById(id)
                .map(correoPersonaMapper::toResponseDTO);
    }

    @Override
    @Cacheable(value = "correosPersonaByPersonaId", key = "#personaId")
    public List<CorreoPersonaResponseDTO> findByPersonaId(Integer personaId) {
        return correoPersonaRepository.findByPersonaId(personaId)
                .stream()
                .map(correoPersonaMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    @CachePut(value = "correoPersonaById", key = "#result.id")
    @CacheEvict(
        value = { 
            "correosPersona", "correosPersonaByPersonaId", 
            "personas", "personaById", "personasByEmail", "personasByTelefono", "personaDisplay",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion",
            "personasNaturales", "personaNaturalById",
            "categoriasPersona", "categoriaPersonaById", "categoriasPersonaByNombre", "personasPorCategoria", "categoriaDePersona",
            "detallesDocumento", "detalleDocumentoById", "detallesDocumentoByDocumentoId", "detallesDocumentoByNumero", 
            "detallesDocumentoByPersonaNaturalId", "detallesDocumentoByPersonaId",
            "viajeros", "viajeroById", "viajerosByNacionalidad", "viajerosByResidencia",
            "viajerosFrecuentes", "viajeroFrecuenteById", "viajerosFrecuentesByViajeroId",
            "telefonosPersona", "telefonoPersonaById", "telefonosPersonaByPersonaId"
        },
        allEntries = true
    )
    public CorreoPersonaResponseDTO save(CorreoPersonaRequestDTO correoPersonaRequestDTO, Integer personaId) {
                boolean existeCorreo = correoPersonaRepository.existsByEmail(correoPersonaRequestDTO.getEmail());
        if (existeCorreo) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }

        Personas persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + personaId));

        CorreoPersona correoPersona = correoPersonaMapper.toEntity(correoPersonaRequestDTO);
        correoPersona.setPersona(persona);

        return correoPersonaMapper.toResponseDTO(correoPersonaRepository.save(correoPersona));
    }


    @Override
    @Transactional
    @CachePut(value = "correoPersonaById", key = "#correoPersonaId")
    @CacheEvict(
        value = { 
            "correosPersona", "correosPersonaByPersonaId", 
            "personas", "personaById", "personasByEmail", "personasByTelefono", "personaDisplay",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion",
            "personasNaturales", "personaNaturalById",
            "categoriasPersona", "categoriaPersonaById", "categoriasPersonaByNombre", "personasPorCategoria", "categoriaDePersona",
            "detallesDocumento", "detalleDocumentoById", "detallesDocumentoByDocumentoId", "detallesDocumentoByNumero", 
            "detallesDocumentoByPersonaNaturalId", "detallesDocumentoByPersonaId",
            "viajeros", "viajeroById", "viajerosByNacionalidad", "viajerosByResidencia",
            "viajerosFrecuentes", "viajeroFrecuenteById", "viajerosFrecuentesByViajeroId",
            "telefonosPersona", "telefonoPersonaById", "telefonosPersonaByPersonaId"
        },
        allEntries = true
    )
    public CorreoPersonaResponseDTO update(Integer personaId, CorreoPersonaRequestDTO correoPersonaRequestDTO, Integer correoPersonaId) {
        CorreoPersona correo = correoPersonaRepository.findById(correoPersonaId)
                .orElseThrow(() -> new RuntimeException("Correo no encontrado con ID: " + correoPersonaId));

        Personas persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + personaId));

        correoPersonaMapper.updateEntityFromDTO(correo, correoPersonaRequestDTO);
        correo.setPersona(persona);
        return correoPersonaMapper.toResponseDTO(correoPersonaRepository.save(correo));
    }


    @Override
    @Transactional
    @CacheEvict(
        value = { 
            "correosPersona", "correoPersonaById", "correosPersonaByPersonaId",
            "personas", "personaById", "personasByEmail", "personasByTelefono", "personaDisplay",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion",
            "personasNaturales", "personaNaturalById",
            "categoriasPersona", "categoriaPersonaById", "categoriasPersonaByNombre", "personasPorCategoria", "categoriaDePersona",
            "detallesDocumento", "detalleDocumentoById", "detallesDocumentoByDocumentoId", "detallesDocumentoByNumero", 
            "detallesDocumentoByPersonaNaturalId", "detallesDocumentoByPersonaId",
            "viajeros", "viajeroById", "viajerosByNacionalidad", "viajerosByResidencia",
            "viajerosFrecuentes", "viajeroFrecuenteById", "viajerosFrecuentesByViajeroId",
            "telefonosPersona", "telefonoPersonaById", "telefonosPersonaByPersonaId"
        }, 
        allEntries = true
    )
    public void deleteById(Integer id) {
        if (!correoPersonaRepository.existsById(id)) {
            throw new RuntimeException("Correo no encontrado con ID: " + id);
        }
        correoPersonaRepository.deleteById(id);
    }
}
