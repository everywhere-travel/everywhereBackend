package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.DetalleLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.DetalleLiquidacion;
import com.everywhere.backend.repository.DetalleLiquidacionRepository;
import com.everywhere.backend.service.DetalleLiquidacionService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DetalleLiquidacionMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetalleLiquidacionServiceImpl implements DetalleLiquidacionService {

    private final DetalleLiquidacionRepository detalleLiquidacionRepository;
    private final DetalleLiquidacionMapper detalleLiquidacionMapper;

    @Override
    public List<DetalleLiquidacionResponseDTO> findAll() {
        return detalleLiquidacionRepository.findAllWithRelations().stream()
                .map(detalleLiquidacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DetalleLiquidacionResponseDTO findById(Integer id) {
        DetalleLiquidacion detalleLiquidacion = detalleLiquidacionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id));
        return detalleLiquidacionMapper.toResponseDTO(detalleLiquidacion);
    }

    @Override
    public List<DetalleLiquidacionResponseDTO> findByLiquidacionId(Integer liquidacionId) {
        List<DetalleLiquidacion> detalles = detalleLiquidacionRepository.findByLiquidacionIdWithRelations(liquidacionId);
        return detalles.stream()
                .map(detalleLiquidacionMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DetalleLiquidacionResponseDTO save(DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacion detalleLiquidacion = detalleLiquidacionMapper.toEntity(detalleLiquidacionRequestDTO);
        detalleLiquidacion.setCreado(LocalDateTime.now());
        detalleLiquidacion = detalleLiquidacionRepository.save(detalleLiquidacion);

        // Fetch la entidad completa con relaciones después de guardar
        DetalleLiquidacion detalleCompleto = detalleLiquidacionRepository.findByIdWithRelations(detalleLiquidacion.getId())
                .orElse(detalleLiquidacion);

        return detalleLiquidacionMapper.toResponseDTO(detalleCompleto);
    }

    @Override
    public DetalleLiquidacionResponseDTO update(Integer id, DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacion existingDetalle = detalleLiquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id));

        detalleLiquidacionMapper.updateEntityFromDTO(detalleLiquidacionRequestDTO, existingDetalle);
        existingDetalle = detalleLiquidacionRepository.save(existingDetalle);

        // Fetch la entidad completa con relaciones después de actualizar
        DetalleLiquidacion detalleCompleto = detalleLiquidacionRepository.findByIdWithRelations(existingDetalle.getId())
                .orElse(existingDetalle);

        return detalleLiquidacionMapper.toResponseDTO(detalleCompleto);
    }

    @Override
    public void deleteById(Integer id) {
        if (!detalleLiquidacionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id);
        }
        detalleLiquidacionRepository.deleteById(id);
    }
}
