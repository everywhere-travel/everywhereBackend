package com.everywhere.backend.service.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.everywhere.backend.service.CategoriaPersonaService;
import com.everywhere.backend.repository.CategoriaPersonaRepository;
import com.everywhere.backend.repository.PersonaNaturalRepository;
import com.everywhere.backend.mapper.CategoriaPersonaMapper;
import com.everywhere.backend.mapper.PersonaNaturalMapper;
import com.everywhere.backend.model.dto.CategoriaPersonaRequestDTO;
import com.everywhere.backend.model.dto.CategoriaPersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaNaturalResponseDTO;
import com.everywhere.backend.model.entity.CategoriaPersona;
import com.everywhere.backend.model.entity.PersonaNatural;
import com.everywhere.backend.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaPersonaServiceImpl implements CategoriaPersonaService {
    
    private final CategoriaPersonaRepository categoriaPersonaRepository;
    private final CategoriaPersonaMapper categoriaPersonaMapper;
    private final PersonaNaturalRepository personaNaturalRepository;
    private final PersonaNaturalMapper personaNaturalMapper;

    @Override
    public List<CategoriaPersonaResponseDTO> findAll() {
        List<CategoriaPersona> categorias = categoriaPersonaRepository.findAll();
        return categorias.stream().map(categoriaPersonaMapper::toResponseDTO).toList();
    }

    @Override
    public CategoriaPersonaResponseDTO findById(Integer id) {
        CategoriaPersona categoria = categoriaPersonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de persona no encontrada con ID: " + id));
        return categoriaPersonaMapper.toResponseDTO(categoria);
    }

    @Override
    public List<CategoriaPersonaResponseDTO> findByNombre(String nombre) {
        List<CategoriaPersona> categorias = categoriaPersonaRepository.findByNombreContainingIgnoreCase(nombre);
        if (categorias.isEmpty())
            throw new ResourceNotFoundException("No existen categorías de persona con el nombre que contiene: " + nombre);
        return categorias.stream().map(categoriaPersonaMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public CategoriaPersonaResponseDTO save(CategoriaPersonaRequestDTO categoriaPersonaRequestDTO) {
        if (categoriaPersonaRepository.existsByNombreIgnoreCase(categoriaPersonaRequestDTO.getNombre()))
            throw new DataIntegrityViolationException("Ya existe una categoría con el nombre: " + categoriaPersonaRequestDTO.getNombre());
        CategoriaPersona categoria = categoriaPersonaMapper.toEntity(categoriaPersonaRequestDTO); 
        return categoriaPersonaMapper.toResponseDTO(categoriaPersonaRepository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaPersonaResponseDTO patch(Integer id, CategoriaPersonaRequestDTO categoriaPersonaRequestDTO) {
        CategoriaPersona categoriaPersona = categoriaPersonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de persona no encontrada con ID: " + id));
        
        if (categoriaPersonaRequestDTO.getNombre() != null && 
            !categoriaPersonaRequestDTO.getNombre().equalsIgnoreCase(categoriaPersona.getNombre()) &&
            categoriaPersonaRepository.existsByNombreIgnoreCase(categoriaPersonaRequestDTO.getNombre())) {
            throw new DataIntegrityViolationException("Ya existe una categoría con el nombre: " + categoriaPersonaRequestDTO.getNombre());
        }
        
        categoriaPersonaMapper.updateEntityFromDTO(categoriaPersonaRequestDTO, categoriaPersona); 
        return categoriaPersonaMapper.toResponseDTO(categoriaPersonaRepository.save(categoriaPersona));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!categoriaPersonaRepository.existsById(id))
            throw new ResourceNotFoundException("Categoría de persona no encontrada con ID: " + id);
        categoriaPersonaRepository.deleteById(id);
    }

    @Override
    public PersonaNaturalResponseDTO asignarCategoria(Integer personaNaturalId, Integer categoriaId) {
        PersonaNatural personaNatural = personaNaturalRepository.findById(personaNaturalId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId));
    
        CategoriaPersona categoria = categoriaPersonaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId));
        
        personaNatural.setCategoriaPersona(categoria); 
        return personaNaturalMapper.toResponseDTO(personaNaturalRepository.save(personaNatural));
    }

    @Override
    public PersonaNaturalResponseDTO desasignarCategoria(Integer personaNaturalId) {
        PersonaNatural personaNatural = personaNaturalRepository.findById(personaNaturalId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId));
        
        personaNatural.setCategoriaPersona(null); 
        return personaNaturalMapper.toResponseDTO(personaNaturalRepository.save(personaNatural));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonaNaturalResponseDTO> findPersonasPorCategoria(Integer categoriaId) {
        if (!categoriaPersonaRepository.existsById(categoriaId))
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId);

        List<PersonaNatural> personasNaturales = personaNaturalRepository.findByCategoriaPersonaId(categoriaId);
        return personasNaturales.stream().map(personaNaturalMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaPersonaResponseDTO getCategoriaDePersona(Integer personaNaturalId) {
        PersonaNatural personaNatural = personaNaturalRepository.findById(personaNaturalId)
            .orElseThrow(() -> new ResourceNotFoundException("Persona natural no encontrada con ID: " + personaNaturalId));

        CategoriaPersona categoriaPersona = personaNatural.getCategoriaPersona();
        if (categoriaPersona == null)
            throw new ResourceNotFoundException("La persona natural con ID: " + personaNaturalId + " no tiene categoría asignada.");
            
        return categoriaPersonaMapper.toResponseDTO(categoriaPersona);
    }
}