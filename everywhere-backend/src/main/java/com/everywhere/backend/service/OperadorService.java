package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.OperadorRequestDTO;
import com.everywhere.backend.model.dto.OperadorResponseDTO;

import java.util.List;
import java.util.Optional;

public interface OperadorService {

    List<OperadorResponseDTO> findAll();

    Optional<OperadorResponseDTO> findByNombre(String nombre);

    Optional<OperadorResponseDTO> findById(int id);

    OperadorResponseDTO save(OperadorRequestDTO dto);

    OperadorResponseDTO update(int id, OperadorRequestDTO dto);

    void deleteById(int id);
}
