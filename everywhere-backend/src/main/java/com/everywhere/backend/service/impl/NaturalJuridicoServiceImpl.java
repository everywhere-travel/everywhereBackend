package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.NaturalJuridicoRequestDTO;
import com.everywhere.backend.model.dto.NaturalJuridicoResponseDTO;
import com.everywhere.backend.model.dto.NaturalJuridicoPatchDTO;
import com.everywhere.backend.model.entity.NaturalJuridico;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.model.entity.PersonaJuridica;
import com.everywhere.backend.repository.NaturalJuridicoRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.repository.PersonaJuridicaRepository;
import com.everywhere.backend.service.NaturalJuridicoService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.mapper.NaturalJuridicoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NaturalJuridicoServiceImpl implements NaturalJuridicoService {

    private final NaturalJuridicoRepository naturalJuridicoRepository;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaJuridicaRepository personaJuridicaRepository;
    private final NaturalJuridicoMapper naturalJuridicoMapper;

    @Override
    @Transactional
    public List<NaturalJuridicoResponseDTO> crearRelaciones(NaturalJuridicoRequestDTO naturalJuridicoRequestDTO) {
        PersonaNatural personaNatural = personaNaturalRepository.findById(naturalJuridicoRequestDTO.getPersonaNaturalId())
                .orElseThrow(() -> new ResourceNotFoundException("Persona natural no encontrada con ID: " + naturalJuridicoRequestDTO.getPersonaNaturalId()));

        List<NaturalJuridico> relacionesCreadas = new ArrayList<>();

        for (Integer personaJuridicaId : naturalJuridicoRequestDTO.getPersonasJuridicasIds()) { // Verificar que la persona jurídica exista
            PersonaJuridica personaJuridica = personaJuridicaRepository.findById(personaJuridicaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + personaJuridicaId));

            Optional<NaturalJuridico> relacionExistente = naturalJuridicoRepository // Verificar que no exista ya la relación
                    .findByPersonaNaturalIdAndPersonaJuridicaId(naturalJuridicoRequestDTO.getPersonaNaturalId(), personaJuridicaId);
            
            if (relacionExistente.isPresent())
                throw new BadRequestException("Ya existe una relación entre la persona natural " + naturalJuridicoRequestDTO.getPersonaNaturalId() + " y la persona jurídica " + personaJuridicaId);

            NaturalJuridico nuevaRelacion = new NaturalJuridico(); // Crear nueva relación
            nuevaRelacion.setPersonaNatural(personaNatural);
            nuevaRelacion.setPersonaJuridica(personaJuridica);

            relacionesCreadas.add(naturalJuridicoRepository.save(nuevaRelacion));
        }
        return relacionesCreadas.stream().map(naturalJuridicoMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<NaturalJuridicoResponseDTO> findByPersonaNaturalId(Integer personaNaturalId) {
        // Verificar que la persona natural exista primero
        if (!personaNaturalRepository.existsById(personaNaturalId))
            throw new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId);
        
        List<NaturalJuridico> relaciones = naturalJuridicoRepository.findByPersonaNaturalId(personaNaturalId);
        if (relaciones.isEmpty())
            throw new ResourceNotFoundException("No se encontraron relaciones para la persona natural con ID: " + personaNaturalId);
        
        return relaciones.stream().map(naturalJuridicoMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<NaturalJuridicoResponseDTO> findByPersonaJuridicaId(Integer personaJuridicaId) {
        if (!personaJuridicaRepository.existsById(personaJuridicaId)) // Verificar que la persona jurídica exista primero
            throw new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + personaJuridicaId);
        
        List<NaturalJuridico> relaciones = naturalJuridicoRepository.findByPersonaJuridicaId(personaJuridicaId);
        if (relaciones.isEmpty())
            throw new ResourceNotFoundException("No se encontraron relaciones para la persona jurídica con ID: " + personaJuridicaId);
        
        return relaciones.stream().map(naturalJuridicoMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public NaturalJuridicoResponseDTO findById(Integer id) {
        NaturalJuridico relacion = naturalJuridicoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Relación no encontrada con ID: " + id));
        return naturalJuridicoMapper.toResponseDTO(relacion);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!naturalJuridicoRepository.existsById(id)) throw new ResourceNotFoundException("Relación no encontrada con ID: " + id);

        naturalJuridicoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByPersonas(Integer personaNaturalId, Integer personaJuridicaId) {
        Optional<NaturalJuridico> relacion = naturalJuridicoRepository.findByPersonaNaturalIdAndPersonaJuridicaId(personaNaturalId, personaJuridicaId);
        if (relacion.isEmpty())
            throw new ResourceNotFoundException("No existe relación entre la persona natural " + personaNaturalId + " y la persona jurídica " + personaJuridicaId);
        
        naturalJuridicoRepository.deleteByPersonaNaturalIdAndPersonaJuridicaId(personaNaturalId, personaJuridicaId);
    }

    @Override
    public List<NaturalJuridicoResponseDTO> findAll() {
        List<NaturalJuridico> relaciones = naturalJuridicoRepository.findAll();
        if (relaciones.isEmpty())
            throw new ResourceNotFoundException("No existen relaciones registradas en el sistema");
        return relaciones.stream().map(naturalJuridicoMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<NaturalJuridicoResponseDTO> patchRelacionesPersonaNatural(Integer personaNaturalId, NaturalJuridicoPatchDTO naturalJuridicoPatchDTO) {
        PersonaNatural personaNatural = personaNaturalRepository.findById(personaNaturalId) // Verificar que la persona natural exista
                .orElseThrow(() -> new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId));

        if (naturalJuridicoPatchDTO.getEliminar() != null && !naturalJuridicoPatchDTO.getEliminar().isEmpty()) { // 1. ELIMINAR relaciones especificadas
            for (Integer personaJuridicaId : naturalJuridicoPatchDTO.getEliminar()) {
                Optional<NaturalJuridico> relacionExistente = naturalJuridicoRepository
                        .findByPersonaNaturalIdAndPersonaJuridicaId(personaNaturalId, personaJuridicaId);

                if (relacionExistente.isPresent()) naturalJuridicoRepository.deleteById(relacionExistente.get().getId());
            }
        }

        if (naturalJuridicoPatchDTO.getAgregar() != null && !naturalJuridicoPatchDTO.getAgregar().isEmpty()) { // 2. AGREGAR nuevas relaciones especificadas
            for (Integer personaJuridicaId : naturalJuridicoPatchDTO.getAgregar()) {
                PersonaJuridica personaJuridica = personaJuridicaRepository.findById(personaJuridicaId) // Verificar que la persona jurídica exista
                        .orElseThrow(() -> new ResourceNotFoundException("Persona jurídica no encontrada con ID: " + personaJuridicaId));

                Optional<NaturalJuridico> relacionExistente = naturalJuridicoRepository // Verificar que no exista ya la relación
                        .findByPersonaNaturalIdAndPersonaJuridicaId(personaNaturalId, personaJuridicaId);
                
                if (relacionExistente.isEmpty()) { // Crear nueva relación
                    NaturalJuridico nuevaRelacion = new NaturalJuridico();
                    nuevaRelacion.setPersonaNatural(personaNatural);
                    nuevaRelacion.setPersonaJuridica(personaJuridica);
                    naturalJuridicoRepository.save(nuevaRelacion);
                }
            }
        }
        return findByPersonaNaturalId(personaNaturalId);
    }
}