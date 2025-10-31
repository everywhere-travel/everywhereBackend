package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.EstadoCotizacionMapper;
import com.everywhere.backend.model.dto.EstadoCotizacionRequestDTO;
import com.everywhere.backend.model.dto.EstadoCotizacionResponseDTO;
import com.everywhere.backend.model.entity.EstadoCotizacion;
import com.everywhere.backend.repository.EstadoCotizacionRepository;
import com.everywhere.backend.service.EstadoCotizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoCotizacionServiceImpl implements EstadoCotizacionService {

    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final EstadoCotizacionMapper estadoCotizacionMapper;

    @Override
    public EstadoCotizacionResponseDTO create(EstadoCotizacionRequestDTO estadoCotizacionRequestDTO) {
        EstadoCotizacion estadoCotizacion = estadoCotizacionMapper.toEntity(estadoCotizacionRequestDTO); 
        return estadoCotizacionMapper.toResponseDTO(estadoCotizacionRepository.save(estadoCotizacion));
    }

    @Override
    public EstadoCotizacionResponseDTO update(Integer id, EstadoCotizacionRequestDTO estadoCotizacionRequestDTO) {
        EstadoCotizacion existing = estadoCotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado de Cotización no encontrado con ID: " + id));

        estadoCotizacionMapper.updateEntityFromDTO(estadoCotizacionRequestDTO, existing); 
        return estadoCotizacionMapper.toResponseDTO(estadoCotizacionRepository.save(existing));
    }

    @Override
    public EstadoCotizacionResponseDTO getById(Integer id) {
        return estadoCotizacionRepository.findById(id).map(estadoCotizacionMapper::toResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Estado de Cotización no encontrado con ID: " + id));
    }

    @Override
    public List<EstadoCotizacionResponseDTO> getAll() {
        return estadoCotizacionRepository.findAll().stream().map(estadoCotizacionMapper::toResponseDTO).toList();
    }

    @Override
    public void delete(Integer id) {
        if (!estadoCotizacionRepository.existsById(id))
            throw new ResourceNotFoundException("Estado de Cotización no encontrado con ID: " + id);
        estadoCotizacionRepository.deleteById(id);
    }
}