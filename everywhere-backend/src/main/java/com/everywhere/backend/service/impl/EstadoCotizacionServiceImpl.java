package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.EstadoCotizacionMapper;
import com.everywhere.backend.model.dto.EstadoCotizacionRequestDTO;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDTO;
import com.everywhere.backend.model.entity.EstadoCotizacion;
import com.everywhere.backend.repository.EstadoCotizacionRepository;
import com.everywhere.backend.service.EstadoCotizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EstadoCotizacionServiceImpl implements EstadoCotizacionService {

    private final EstadoCotizacionRepository estadoCotizacionRepository;

    @Override
    public EstadoCotizacionResponseDTO create(EstadoCotizacionRequestDTO dto) {
        EstadoCotizacion entity = EstadoCotizacionMapper.toEntity(dto);
        EstadoCotizacion saved = estadoCotizacionRepository.save(entity);
        return EstadoCotizacionMapper.toResponse(saved);
    }

    @Override
    public EstadoCotizacionResponseDTO update(Integer id, EstadoCotizacionRequestDTO dto) {
        EstadoCotizacion entity = estadoCotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado de Cotización no encontrado"));

        entity.setDescripcion(dto.getDescripcion());
        EstadoCotizacion updated = estadoCotizacionRepository.save(entity);

        return EstadoCotizacionMapper.toResponse(updated);
    }

    @Override
    public Optional<EstadoCotizacionResponseDTO> getById(Integer id) {
        return estadoCotizacionRepository.findById(id)
                .map(EstadoCotizacionMapper::toResponse);
    }

    @Override
    public List<EstadoCotizacionResponseDTO> getAll() {
        return estadoCotizacionRepository.findAll()
                .stream()
                .map(EstadoCotizacionMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Integer id) {
        if (!estadoCotizacionRepository.existsById(id)) {
            throw new RuntimeException("Estado de Cotización no encontrado");
        }
        estadoCotizacionRepository.deleteById(id);
    }
}