package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CarpetaRequestDto;
import com.everywhere.backend.model.dto.CarpetaResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CarpetaService {

    // CRUD
    CarpetaResponseDto create(CarpetaRequestDto dto, Integer carpetaPadreId);
    Optional<CarpetaResponseDto> findById(Integer id);
    List<CarpetaResponseDto> findAll();
    CarpetaResponseDto update(Integer id, CarpetaRequestDto dto);
    void delete(Integer id);

    // Listar carpetas por hijo de un ID padre
    List<CarpetaResponseDto> findByCarpetaPadreId(Integer carpetaPadreId);

    // Listar carpetas por nivel
    List<CarpetaResponseDto> findByNivel(Integer nivel);

    // Listar carpetas por nombre
    List<CarpetaResponseDto> findByNombre(String nombre);

    // Listar carpetas por año/mes
    List<CarpetaResponseDto> findByMes(int mes);

    // Listar carpetas por rango de días
    List<CarpetaResponseDto> findByFechaCreacionBetween(LocalDate inicio, LocalDate fin);

    // Buscar carpetas recientes
    List<CarpetaResponseDto> findRecent(int limit);

    // Buscar carpetas raíz
    List<CarpetaResponseDto> findRaices();

    // Lista la ruta por donde navegas
    List<CarpetaResponseDto> findCamino(Integer carpetaId);

    //Listar Hijos por id de padre
    List<CarpetaResponseDto> findHijosByPadreId(Integer carpetaPadreId);
}
