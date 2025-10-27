package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ObservacionLiquidacionMapper;
import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObservacionLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.ObservacionLiquidacion;
import com.everywhere.backend.repository.ObservacionLiquidacionRepository;
import com.everywhere.backend.service.ObservacionLiquidacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObservacionLiquidacionServiceImpl implements ObservacionLiquidacionService {

    private final ObservacionLiquidacionRepository observacionLiquidacionRepository;
    private final ObservacionLiquidacionMapper observacionLiquidacionMapper;

    @Override
    public List<ObservacionLiquidacionResponseDTO> findAll() {
        return observacionLiquidacionRepository.findAll()
                .stream()
                .map(observacionLiquidacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ObservacionLiquidacionResponseDTO findById(Long id) {
        ObservacionLiquidacion entity = observacionLiquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Observación de liquidación no encontrada con ID: " + id));
        return observacionLiquidacionMapper.toResponseDTO(entity);
    }


    @Override
    public ObservacionLiquidacionResponseDTO save(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) {
        ObservacionLiquidacion entity = observacionLiquidacionMapper.toEntity(observacionLiquidacionRequestDTO);
        ObservacionLiquidacion saved = observacionLiquidacionRepository.save(entity);
        return observacionLiquidacionMapper.toResponseDTO(saved);
    }

    @Override
    public ObservacionLiquidacionResponseDTO update(Long id, ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) {
        ObservacionLiquidacion existing = observacionLiquidacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Observación de liquidación no encontrada con ID: " + id));

        observacionLiquidacionMapper.updateEntityFromDTO(observacionLiquidacionRequestDTO, existing);

        ObservacionLiquidacion updated = observacionLiquidacionRepository.save(existing);
        return observacionLiquidacionMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteById(Long id) {
        if (!observacionLiquidacionRepository.existsById(id)) {
            throw new RuntimeException("No existe una observación de liquidación con ID: " + id);
        }
        observacionLiquidacionRepository.deleteById(id);
    }

    @Override
    public List<ObservacionLiquidacionResponseDTO> findByLiquidacionId(Integer liquidacionId) {
        List<ObservacionLiquidacion> observaciones = observacionLiquidacionRepository.findByLiquidacionId(liquidacionId);
        return observaciones.stream()
                .map(observacionLiquidacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
