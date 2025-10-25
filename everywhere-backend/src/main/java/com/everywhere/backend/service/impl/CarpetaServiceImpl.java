package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CarpetaMapper;
import com.everywhere.backend.model.dto.CarpetaRequestDto;
import com.everywhere.backend.model.dto.CarpetaResponseDto;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.repository.CarpetaRepository;
import com.everywhere.backend.service.CarpetaService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarpetaServiceImpl implements CarpetaService {

    private final CarpetaRepository carpetaRepository;
    private final CarpetaMapper carpetaMapper;

    @Override
    public CarpetaResponseDto create(CarpetaRequestDto carpetaRequestDto, Integer carpetaPadreId) {
        Carpeta carpeta = carpetaMapper.toEntity(carpetaRequestDto);

        if (carpetaPadreId != null) {
            Carpeta carpetaPadre = carpetaRepository.findById(carpetaPadreId)
                    .orElseThrow(() -> new ResourceNotFoundException("Carpeta padre no encontrada con ID: " + carpetaPadreId));
            carpeta.setCarpetaPadre(carpetaPadre);
            carpeta.setNivel(carpetaPadre.getNivel() + 1);
        } else {
            carpeta.setNivel(0); // raíz
        }

        Carpeta carpetaSaved = carpetaRepository.save(carpeta);
        return carpetaMapper.toResponse(carpetaSaved);
    }

    @Override
    public Optional<CarpetaResponseDto> findById(Integer id) {
        return carpetaRepository.findById(id).map(carpetaMapper::toResponse);
    }

    @Override
    public List<CarpetaResponseDto> findAll() {
        return mapToResponseList(carpetaRepository.findAll());
    }

    @Override
    public CarpetaResponseDto update(Integer id, CarpetaRequestDto carpetaRequestDto) {
        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID: " + id));

        carpeta.setNombre(carpetaRequestDto.getNombre());
        carpeta.setDescripcion(carpetaRequestDto.getDescripcion());

        Carpeta updated = carpetaRepository.save(carpeta);
        return carpetaMapper.toResponse(updated);
    }

    @Override
    public void delete(Integer id) {
        if (!carpetaRepository.existsById(id)) throw new ResourceNotFoundException("Carpeta no encontrada con ID: " + id);
        carpetaRepository.deleteById(id);
    }

    @Override
    public List<CarpetaResponseDto> findByCarpetaPadreId(Integer carpetaPadreId) {
        return mapToResponseList(carpetaRepository.findByCarpetaPadreId(carpetaPadreId));
    }

    @Override
    public List<CarpetaResponseDto> findByNivel(Integer nivel) {
        return mapToResponseList(carpetaRepository.findByNivel(nivel));
    }

    @Override
    public List<CarpetaResponseDto> findByNombre(String nombre) {
        return mapToResponseList(carpetaRepository.findByNombreContainingIgnoreCase(nombre));
    }

    @Override
    public List<CarpetaResponseDto> findByMes(int mes) {
        int anioActual = LocalDate.now().getYear();
        return mapToResponseList(carpetaRepository.findByAnioAndMes(anioActual, mes));
    }

    @Override
    public List<CarpetaResponseDto> findByFechaCreacionBetween(LocalDate inicio, LocalDate fin) {
        LocalDateTime start = inicio.atStartOfDay();
        LocalDateTime end = fin.plusDays(1).atStartOfDay().minusSeconds(1);
        return mapToResponseList(carpetaRepository.findByCreadoBetweenOrderByCreadoAsc(start, end));
    }

    @Override
    public List<CarpetaResponseDto> findRecent(int limit) {
        return mapToResponseList(carpetaRepository.findAll(PageRequest.of(0, limit, Sort.by("creado").descending())).getContent());
    }

    @Override
    public List<CarpetaResponseDto> findRaices() {
        return mapToResponseList(carpetaRepository.findByCarpetaPadreIsNull());
    }

    @Override
    public List<CarpetaResponseDto> findCamino(Integer carpetaId) {
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Carpeta no encontrada con ID: " + carpetaId));

        List<Carpeta> camino = new ArrayList<>();

        while (carpeta != null) { // Recorremos hacia arriba hasta la raíz
            camino.add(carpeta);
            carpeta = carpeta.getCarpetaPadre();
        }
        Collections.reverse(camino); // Invertimos para que quede desde la raíz hasta la carpeta actual
        return mapToResponseList(camino);
    }

    @Override
    public List<CarpetaResponseDto> findHijosByPadreId(Integer carpetaPadreId) {
        return mapToResponseList(carpetaRepository.findByCarpetaPadreId(carpetaPadreId));
    }

    private List<CarpetaResponseDto> mapToResponseList(List<Carpeta> carpetas) {
        return carpetas.stream().map(carpetaMapper::toResponse).collect(Collectors.toList());
    }
}