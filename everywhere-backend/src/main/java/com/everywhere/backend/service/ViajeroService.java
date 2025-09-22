package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.ViajeroRequestDTO;
import com.everywhere.backend.model.dto.ViajeroResponseDTO;
import com.everywhere.backend.model.entity.Viajero;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface ViajeroService {
    List<ViajeroResponseDTO> findAll();
    ViajeroResponseDTO findById(Integer id);
    List<ViajeroResponseDTO> findByNombres(String nombres);
    List<ViajeroResponseDTO> findByNumeroDocumento(String numeroDocumento);
    List<ViajeroResponseDTO> findByNacionalidad(String nacionalidad);
    List<ViajeroResponseDTO> findByResidencia(String residencia);
    List<ViajeroResponseDTO> findByFechaVencimientoDocumento(LocalDate fechaVencimiento);
    List<ViajeroResponseDTO> findByFechaVencimientoDocumentoBetween(LocalDate fechaInicio, LocalDate fechaFin);
    ViajeroResponseDTO save(ViajeroRequestDTO viajeroRequestDTO);
    ViajeroResponseDTO update(Integer id, ViajeroRequestDTO viajeroRequestDTO);
    void deleteById(Integer id);
    ByteArrayInputStream exportToExcelCosta(List<Viajero> viajeros) throws IOException;
}
