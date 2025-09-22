package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CarpetaMapper;
import com.everywhere.backend.model.dto.CarpetaRequestDto;
import com.everywhere.backend.model.dto.CarpetaResponseDto;
import com.everywhere.backend.model.entity.Carpeta;
import com.everywhere.backend.repository.CarpetaRepository;
import com.everywhere.backend.service.CarpetaService;
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
public class CarpetaServiceImpl implements CarpetaService {

    private final CarpetaRepository carpetaRepository;

    public CarpetaServiceImpl(CarpetaRepository carpetaRepository) {
        this.carpetaRepository = carpetaRepository;
    }

    @Override
    public CarpetaResponseDto create(CarpetaRequestDto dto, Integer carpetaPadreId) {
        Carpeta carpeta = CarpetaMapper.toEntity(dto);

        if (carpetaPadreId != null) {
            Carpeta carpetaPadre = carpetaRepository.findById(carpetaPadreId)
                    .orElseThrow(() -> new RuntimeException("Carpeta padre no encontrada"));
            carpeta.setCarpetaPadre(carpetaPadre);
            carpeta.setNivel(carpetaPadre.getNivel() + 1);
        } else {
            carpeta.setNivel(0); // raíz
        }

        carpeta.setCreado(LocalDateTime.now());
        carpeta.setActualizado(LocalDateTime.now());

        Carpeta saved = carpetaRepository.save(carpeta);
        return CarpetaMapper.toResponse(saved);
    }

    @Override
    public Optional<CarpetaResponseDto> findById(Integer id) {
        return carpetaRepository.findById(id).map(CarpetaMapper::toResponse);
    }

    @Override
    public List<CarpetaResponseDto> findAll() {
        return carpetaRepository.findAll()
                .stream()
                .map(CarpetaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CarpetaResponseDto update(Integer id, CarpetaRequestDto dto) {
        Carpeta carpeta = carpetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carpeta no encontrada"));

        carpeta.setNombre(dto.getNombre());
        carpeta.setDescripcion(dto.getDescripcion());
        carpeta.setActualizado(LocalDateTime.now());

        Carpeta updated = carpetaRepository.save(carpeta);
        return CarpetaMapper.toResponse(updated);
    }

    @Override
    public void delete(Integer id) {
        if (!carpetaRepository.existsById(id)) {
            throw new RuntimeException("Carpeta no encontrada");
        }
        carpetaRepository.deleteById(id);
    }


    @Override
    public List<CarpetaResponseDto> findByCarpetaPadreId(Integer carpetaPadreId) {
        return carpetaRepository.findByCarpetaPadreId(carpetaPadreId)
                .stream()
                .map(CarpetaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarpetaResponseDto> findByNivel(Integer nivel) {
        return carpetaRepository.findByNivel(nivel)
                .stream()
                .map(CarpetaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarpetaResponseDto> findByNombre(String nombre) {
        return carpetaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(CarpetaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarpetaResponseDto> findByMes(int mes) {
        int anioActual = LocalDate.now().getYear();
        LocalDateTime inicio = LocalDate.of(anioActual, mes, 1).atStartOfDay();
        LocalDateTime fin = inicio.plusMonths(1).minusSeconds(1);

        return carpetaRepository.findByCreadoBetween(inicio, fin)
                .stream()
                .map(CarpetaMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public List<CarpetaResponseDto> findByFechaCreacionBetween(LocalDate inicio, LocalDate fin) {
        LocalDateTime start = inicio.atStartOfDay();
        LocalDateTime end = fin.plusDays(1).atStartOfDay().minusSeconds(1);
        return carpetaRepository.findByCreadoBetweenOrderByCreadoAsc(start, end)
                .stream()
                .map(CarpetaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarpetaResponseDto> findRecent(int limit) {
        return carpetaRepository.findAll(PageRequest.of(0, limit, Sort.by("creado").descending()))
                .stream()
                .map(CarpetaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarpetaResponseDto> findRaices() {
        return carpetaRepository.findByCarpetaPadreIsNull()
                .stream()
                .map(CarpetaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarpetaResponseDto> findCamino(Integer carpetaId) {
        Carpeta carpeta = carpetaRepository.findById(carpetaId)
                .orElseThrow(() -> new RuntimeException("Carpeta no encontrada"));

        List<Carpeta> camino = new ArrayList<>();

        // Recorremos hacia arriba hasta la raíz
        while (carpeta != null) {
            camino.add(carpeta);
            carpeta = carpeta.getCarpetaPadre();
        }

        // Invertimos para que quede desde la raíz hasta la carpeta actual
        Collections.reverse(camino);

        return camino.stream()
                .map(CarpetaMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<CarpetaResponseDto> findHijosByPadreId(Integer carpetaPadreId) {
        return carpetaRepository.findByCarpetaPadreId(carpetaPadreId)
                .stream()
                .map(CarpetaMapper::toResponse)
                .collect(Collectors.toList());
    }

}
