package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CategoriaPersonaRequestDTO;
import com.everywhere.backend.model.dto.CategoriaPersonaResponseDTO;

import java.util.List;

public interface CategoriaPersonaService {
    List<CategoriaPersonaResponseDTO> findAll();
    CategoriaPersonaResponseDTO findById(Integer id);
    List<CategoriaPersonaResponseDTO> findByNombre(String nombre);
    CategoriaPersonaResponseDTO save(CategoriaPersonaRequestDTO categoriaPersonaRequestDTO);
    CategoriaPersonaResponseDTO patch(Integer id, CategoriaPersonaRequestDTO categoriaPersonaRequestDTO);
    void deleteById(Integer id);
}