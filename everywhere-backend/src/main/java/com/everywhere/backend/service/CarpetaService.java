package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CarpetaRequestDTO;
import com.everywhere.backend.model.dto.CarpetaResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CarpetaService {

    // CRUD
    CarpetaResponseDTO create(CarpetaRequestDTO dto, Integer carpetaPadreId);
    Optional<CarpetaResponseDTO> findById(Integer id);
    List<CarpetaResponseDTO> findAll();
    CarpetaResponseDTO update(Integer id, CarpetaRequestDTO dto);
    void delete(Integer id);

    // Listar carpetas por hijo de un ID padre
    List<CarpetaResponseDTO> findByCarpetaPadreId(Integer carpetaPadreId);

    // Listar carpetas por nivel
    List<CarpetaResponseDTO> findByNivel(Integer nivel);

    // Listar carpetas por nombre
    List<CarpetaResponseDTO> findByNombre(String nombre);

    // Listar carpetas por año/mes
    List<CarpetaResponseDTO> findByMes(int mes);

    // Listar carpetas por rango de días
    List<CarpetaResponseDTO> findByFechaCreacionBetween(LocalDate inicio, LocalDate fin);

    // Buscar carpetas recientes
    List<CarpetaResponseDTO> findRecent(int limit);

    // Buscar carpetas raíz
    List<CarpetaResponseDTO> findRaices();

    // Lista la ruta por donde navegas
    List<CarpetaResponseDTO> findCamino(Integer carpetaId);

    //Listar Hijos por id de padre
    List<CarpetaResponseDTO> findHijosByPadreId(Integer carpetaPadreId);
}
