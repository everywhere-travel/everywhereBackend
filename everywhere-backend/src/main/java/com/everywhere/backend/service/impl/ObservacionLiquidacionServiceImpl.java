package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObeservacionLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.ObservacionLiquidacion;
import com.everywhere.backend.repository.ObservacionLiquidacionRepository;
import com.everywhere.backend.service.ObservacionLiquidacionService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ObservacionLiquidacionMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObservacionLiquidacionServiceImpl implements ObservacionLiquidacionService {

    private final ObservacionLiquidacionRepository observacionLiquidacionRepository;
    private final ObservacionLiquidacionMapper observacionLiquidacionMapper;

    @Override
    public List<ObeservacionLiquidacionResponseDTO> findAll() {
        return observacionLiquidacionRepository.findAllWithLiquidacion().stream()
                .map(observacionLiquidacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ObeservacionLiquidacionResponseDTO findById(Long id) {
        ObservacionLiquidacion observacion = observacionLiquidacionRepository.findByIdWithLiquidacion(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observación de liquidación no encontrada con ID: " + id));
        return observacionLiquidacionMapper.toResponseDTO(observacion);
    }

    @Override
    public ObeservacionLiquidacionResponseDTO save(ObservacionLiquidacionRequestDTO requestDTO) {
        ObservacionLiquidacion observacion = observacionLiquidacionMapper.toEntity(requestDTO);
        observacion = observacionLiquidacionRepository.save(observacion);

        // Fetch la entidad completa con relaciones después de guardar
        ObservacionLiquidacion observacionCompleta = observacionLiquidacionRepository.findByIdWithLiquidacion(observacion.getId())
                .orElse(observacion);

        return observacionLiquidacionMapper.toResponseDTO(observacionCompleta);
    }

    @Override
    public ObeservacionLiquidacionResponseDTO update(Long id, ObservacionLiquidacionRequestDTO requestDTO) {
        ObservacionLiquidacion existingObservacion = observacionLiquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observación de liquidación no encontrada con ID: " + id));

        observacionLiquidacionMapper.updateEntityFromDTO(requestDTO, existingObservacion);
        existingObservacion = observacionLiquidacionRepository.save(existingObservacion);

        // Fetch la entidad completa con relaciones después de actualizar
        ObservacionLiquidacion observacionCompleta = observacionLiquidacionRepository.findByIdWithLiquidacion(existingObservacion.getId())
                .orElse(existingObservacion);

        return observacionLiquidacionMapper.toResponseDTO(observacionCompleta);
    }

    @Override
    public void deleteById(Long id) {
        if (!observacionLiquidacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Observación de liquidación no encontrada con ID: " + id);
        }
        observacionLiquidacionRepository.deleteById(id);
    }

    @Override
    public List<ObeservacionLiquidacionResponseDTO> findByLiquidacionId(Integer liquidacionId) {
        return observacionLiquidacionRepository.findByLiquidacionId(liquidacionId).stream()
                .map(observacionLiquidacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
