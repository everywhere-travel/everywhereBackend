package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface ViajeroService {

    List<ViajeroResponseDTO> findAll();
    ViajeroResponseDTO findById(Integer id);
    List<ViajeroResponseDTO> findByNombres(String nombres);
    List<ViajeroResponseDTO> findByNacionalidad(String nacionalidad);
    List<ViajeroResponseDTO> findByResidencia(String residencia);
    ViajeroResponseDTO save(ViajeroRequestDTO viajeroRequestDTO);
    ViajeroResponseDTO update(Integer id, ViajeroRequestDTO viajeroRequestDTO);
    void deleteById(Integer id);
}