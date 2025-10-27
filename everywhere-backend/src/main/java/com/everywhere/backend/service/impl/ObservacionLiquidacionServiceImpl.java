package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ObservacionLiquidacionMapper;
import com.everywhere.backend.model.dto.ObservacionLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.ObservacionLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.ObservacionLiquidacion;
import com.everywhere.backend.repository.LiquidacionRepository;
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
    private final LiquidacionRepository liquidacionRepository;

    @Override
    public List<ObservacionLiquidacionResponseDTO> findAll() {
        return observacionLiquidacionRepository.findAll()
                .stream()
                .map(observacionLiquidacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ObservacionLiquidacionResponseDTO findById(Long id) {
        ObservacionLiquidacion observacionLiquidacion = observacionLiquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Observación de liquidación no encontrada con ID: " + id));

        return observacionLiquidacionMapper.toResponseDTO(observacionLiquidacion);
    }

    @Override
    public ObservacionLiquidacionResponseDTO save(ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) {
        ObservacionLiquidacion observacionLiquidacion = observacionLiquidacionMapper.toEntity(observacionLiquidacionRequestDTO);

        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = liquidacionRepository.findById(observacionLiquidacionRequestDTO.getLiquidacionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Liquidación no encontrada con id " + observacionLiquidacionRequestDTO.getLiquidacionId()));
            observacionLiquidacion.setLiquidacion(liquidacion);
        }
        return observacionLiquidacionMapper.toResponseDTO(
                observacionLiquidacionRepository.save(observacionLiquidacion)
        );
    }

    @Override
    public ObservacionLiquidacionResponseDTO update(Long id, ObservacionLiquidacionRequestDTO observacionLiquidacionRequestDTO) {
        ObservacionLiquidacion observacionLiquidacion = observacionLiquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Observación de liquidación no encontrada con ID: " + id));

        observacionLiquidacionMapper.updateEntityFromDTO(observacionLiquidacionRequestDTO, observacionLiquidacion);

        if (observacionLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = liquidacionRepository.findById(observacionLiquidacionRequestDTO.getLiquidacionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Liquidación no encontrada con id " + observacionLiquidacionRequestDTO.getLiquidacionId()));
            observacionLiquidacion.setLiquidacion(liquidacion);
        }

        return observacionLiquidacionMapper.toResponseDTO(
                observacionLiquidacionRepository.save(observacionLiquidacion)
        );
    }

    @Override
    public void deleteById(Long id) {
        if (!observacionLiquidacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe una observación de liquidación con ID: " + id);
        }
        observacionLiquidacionRepository.deleteById(id);
    }

    @Override
    public List<ObservacionLiquidacionResponseDTO> findByLiquidacionId(Integer liquidacionId) {
        List<ObservacionLiquidacion> observaciones = observacionLiquidacionRepository.findByLiquidacionId(liquidacionId);

        if (observaciones.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron observaciones para la liquidación con ID: " + liquidacionId);
        }

        return observaciones.stream()
                .map(observacionLiquidacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
