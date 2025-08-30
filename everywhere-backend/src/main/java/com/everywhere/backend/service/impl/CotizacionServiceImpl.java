package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.CotizacionMapper;
import com.everywhere.backend.model.dto.CotizacionRequestDto;
import com.everywhere.backend.model.dto.CotizacionResponseDto;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.service.CotizacionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;

    public CotizacionServiceImpl(CotizacionRepository cotizacionRepository) {
        this.cotizacionRepository = cotizacionRepository;
    }

    @Override
    public CotizacionResponseDto create(CotizacionRequestDto dto) {
        Cotizacion entity = new Cotizacion();

        // Mapear datos desde el DTO
        CotizacionMapper.updateEntityFromRequest(entity, dto);

        // Generar código incremental tipo COT-001, COT-002, etc.
        entity.setCodigoCotizacion(generateCodigoCotizacion());

        // Fechas automáticas
        entity.setFechaEmision(LocalDate.now());
        entity.setActualizado(LocalDateTime.now());

        Cotizacion saved = cotizacionRepository.save(entity);
        return CotizacionMapper.toResponse(saved);
    }

    @Override
    public Optional<CotizacionResponseDto> findById(Integer id) {
        return cotizacionRepository.findById(id).map(CotizacionMapper::toResponse);
    }


    @Override
    public List<CotizacionResponseDto> findAll() {
        return cotizacionRepository.findAll()
                .stream()
                .map(CotizacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CotizacionResponseDto update(Integer id, CotizacionRequestDto dto) {
        System.out.println("DEBUG: Buscando cotización con ID = " + id);

        Cotizacion entity = cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        System.out.println("DEBUG: Cotización encontrada = " + entity);

        CotizacionMapper.updateEntityFromRequest(entity, dto);
        entity.setActualizado(LocalDateTime.now());

        Cotizacion updated = cotizacionRepository.save(entity);
        return CotizacionMapper.toResponse(updated);
    }


    @Override
    public void delete(Integer id) {
        cotizacionRepository.deleteById(id);
    }

    private String generateCodigoCotizacion() {
        Integer maxId = cotizacionRepository.findMaxId();
        int next = (maxId != null ? maxId + 1 : 1);
        return String.format("COT-%03d", next);
    }
}
