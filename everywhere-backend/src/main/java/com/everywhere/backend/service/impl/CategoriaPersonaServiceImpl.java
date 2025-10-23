package com.everywhere.backend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.everywhere.backend.service.CategoriaPersonaService;
import com.everywhere.backend.repository.CategoriaPersonaRepository;
import com.everywhere.backend.mapper.CategoriaPersonaMapper;
import com.everywhere.backend.model.dto.CategoriaPersonaRequestDTO;
import com.everywhere.backend.model.dto.CategoriaPersonaResponseDTO;
import com.everywhere.backend.model.entity.CategoriaPersona;
import com.everywhere.backend.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaPersonaServiceImpl implements CategoriaPersonaService {
    
    private final CategoriaPersonaRepository categoriaPersonaRepository;
    private final CategoriaPersonaMapper categoriaPersonaMapper;

    @Override
    public List<CategoriaPersonaResponseDTO> findAll() {
        List<CategoriaPersona> categorias = categoriaPersonaRepository.findAll();
        return categorias.stream()
                .map(categoriaPersonaMapper::toResponseDTO)
                .collect(Collectors.toList());
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
        return categorias.stream()
                .map(categoriaPersonaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoriaPersonaResponseDTO save(CategoriaPersonaRequestDTO categoriaPersonaRequestDTO) {
        // Validar que no exista una categoría con el mismo nombre
        if (categoriaPersonaRepository.existsByNombreIgnoreCase(categoriaPersonaRequestDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaPersonaRequestDTO.getNombre());
        }
        
        CategoriaPersona categoria = categoriaPersonaMapper.toEntity(categoriaPersonaRequestDTO);
        categoria = categoriaPersonaRepository.save(categoria);
        return categoriaPersonaMapper.toResponseDTO(categoria);
    }

    @Override
    @Transactional
    public CategoriaPersonaResponseDTO patch(Integer id, CategoriaPersonaRequestDTO categoriaPersonaRequestDTO) {
        CategoriaPersona categoria = categoriaPersonaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría de persona no encontrada con ID: " + id));
        
        // Validar nombre duplicado solo si se está actualizando el nombre
        if (categoriaPersonaRequestDTO.getNombre() != null && 
            !categoriaPersonaRequestDTO.getNombre().equalsIgnoreCase(categoria.getNombre()) &&
            categoriaPersonaRepository.existsByNombreIgnoreCase(categoriaPersonaRequestDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaPersonaRequestDTO.getNombre());
        }
        
        categoriaPersonaMapper.updateEntityFromDTO(categoriaPersonaRequestDTO, categoria);
        categoria = categoriaPersonaRepository.save(categoria);
        return categoriaPersonaMapper.toResponseDTO(categoria);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!categoriaPersonaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría de persona no encontrada con ID: " + id);
        }
        categoriaPersonaRepository.deleteById(id);
    }
}
