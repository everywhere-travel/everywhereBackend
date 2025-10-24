package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ViajeroFrecuenteMapper;
import com.everywhere.backend.model.dto.ViajeroFrecuenteRequestDto;
import com.everywhere.backend.model.dto.ViajeroFrecuenteResponseDto;
import com.everywhere.backend.model.entity.Viajero;
import com.everywhere.backend.model.entity.ViajeroFrecuente;
import com.everywhere.backend.repository.ViajeroFrecuenteRepository;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.service.ViajeroFrecuenteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViajeroFrecuenteServiceImpl implements ViajeroFrecuenteService {

    private final ViajeroFrecuenteRepository viajeroFrecuenteRepository;
    private final ViajeroRepository viajeroRepository;

    public ViajeroFrecuenteServiceImpl(ViajeroFrecuenteRepository viajeroFrecuenteRepository,
                                       ViajeroRepository viajeroRepository) {
        this.viajeroFrecuenteRepository = viajeroFrecuenteRepository;
        this.viajeroRepository = viajeroRepository;
    }

    @Override
    public ViajeroFrecuenteResponseDto crear(Integer viajeroId, ViajeroFrecuenteRequestDto dto) {
        Viajero viajero = viajeroRepository.findById(viajeroId)
                .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con id: " + viajeroId));

        ViajeroFrecuente entity = ViajeroFrecuenteMapper.toEntity(dto, viajero);
        ViajeroFrecuente saved = viajeroFrecuenteRepository.save(entity);

        return ViajeroFrecuenteMapper.toResponse(saved);
    }

    @Override
    public ViajeroFrecuenteResponseDto buscarPorId(Integer id) {
        ViajeroFrecuente entity = viajeroFrecuenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ViajeroFrecuente no encontrado con id: " + id));

        return ViajeroFrecuenteMapper.toResponse(entity);
    }

    @Override
    public List<ViajeroFrecuenteResponseDto> listarPorViajero(Integer viajeroId) {
        return viajeroFrecuenteRepository.findByViajero_Id(viajeroId)
                .stream()
                .map(ViajeroFrecuenteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Integer id) {
        if (!viajeroFrecuenteRepository.existsById(id)) {
            throw new ResourceNotFoundException("ViajeroFrecuente no encontrado con id: " + id);
        }
        viajeroFrecuenteRepository.deleteById(id);
    }

    @Override
    public ViajeroFrecuenteResponseDto actualizar(Integer id, ViajeroFrecuenteRequestDto dto) {
        ViajeroFrecuente entity = viajeroFrecuenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ViajeroFrecuente no encontrado con id: " + id));

        entity.setAreolinea(dto.getAreolinea());
        entity.setCodigo(dto.getCodigo());
        entity.setActualizado(LocalDateTime.now());

        ViajeroFrecuente updated = viajeroFrecuenteRepository.save(entity);
        return ViajeroFrecuenteMapper.toResponse(updated);
    }

    @Override
    public List<ViajeroFrecuenteResponseDto> buscarPorViajeroId(Integer viajeroId) {
        return viajeroFrecuenteRepository.findByViajero_Id(viajeroId)
                .stream()
                .map(ViajeroFrecuenteMapper::toResponse)
                .collect(Collectors.toList());
    }

}
