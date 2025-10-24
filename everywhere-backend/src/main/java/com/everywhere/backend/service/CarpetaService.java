package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CarpetaRequestDto;
import com.everywhere.backend.model.dto.CarpetaResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CarpetaService {

    CarpetaResponseDto create(CarpetaRequestDto carpetaRequestDto, Integer carpetaPadreId);
    Optional<CarpetaResponseDto> findById(Integer id);
    List<CarpetaResponseDto> findAll();
    CarpetaResponseDto update(Integer id, CarpetaRequestDto carpetaRequestDto);
    void delete(Integer id);
    List<CarpetaResponseDto> findByCarpetaPadreId(Integer carpetaPadreId);
    List<CarpetaResponseDto> findByNivel(Integer nivel);
    List<CarpetaResponseDto> findByNombre(String nombre);
    List<CarpetaResponseDto> findByMes(int mes);
    List<CarpetaResponseDto> findByFechaCreacionBetween(LocalDate inicio, LocalDate fin);
    List<CarpetaResponseDto> findRecent(int limit);
    List<CarpetaResponseDto> findRaices();
    List<CarpetaResponseDto> findCamino(Integer carpetaId);
    List<CarpetaResponseDto> findHijosByPadreId(Integer carpetaPadreId);
}