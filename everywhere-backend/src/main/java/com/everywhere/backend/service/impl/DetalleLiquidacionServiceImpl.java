package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.DetalleLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionSinLiquidacionDTO;
import com.everywhere.backend.model.entity.DetalleLiquidacion;
import com.everywhere.backend.repository.DetalleLiquidacionRepository;
import com.everywhere.backend.repository.LiquidacionRepository;
import com.everywhere.backend.repository.OperadorRepository;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.repository.ProveedorRepository;
import com.everywhere.backend.repository.ViajeroRepository;
import com.everywhere.backend.service.DetalleLiquidacionService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DetalleLiquidacionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DetalleLiquidacionServiceImpl implements DetalleLiquidacionService {

    private final DetalleLiquidacionRepository detalleLiquidacionRepository;
    private final DetalleLiquidacionMapper detalleLiquidacionMapper;
    private final LiquidacionRepository liquidacionRepository;
    private final OperadorRepository operadorRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final ViajeroRepository viajeroRepository;


    @Override
    public List<DetalleLiquidacionResponseDTO> findAll() {
        return mapToResponseList(detalleLiquidacionRepository.findAllWithRelations());
    }

    @Override
    public DetalleLiquidacionResponseDTO findById(Integer id) {
        DetalleLiquidacion detalleLiquidacion = detalleLiquidacionRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id));
        return detalleLiquidacionMapper.toResponseDTO(detalleLiquidacion);
    }

    @Override
    public List<DetalleLiquidacionResponseDTO> findByLiquidacionId(Integer liquidacionId) {
        return mapToResponseList(detalleLiquidacionRepository.findByLiquidacionIdWithRelations(liquidacionId));
    }

    @Override
    public List<DetalleLiquidacionSinLiquidacionDTO> findByLiquidacionIdSinLiquidacion(Integer liquidacionId) {
        return mapToSinLiquidacionList(detalleLiquidacionRepository.findByLiquidacionIdSinLiquidacion(liquidacionId));
    }

    @Override
    public DetalleLiquidacionResponseDTO save(DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacion detalleLiquidacion = detalleLiquidacionMapper.toEntity(detalleLiquidacionRequestDTO);

        if(detalleLiquidacionRequestDTO.getLiquidacionId() != null) {
            detalleLiquidacion.setLiquidacion(liquidacionRepository.getReferenceById(detalleLiquidacionRequestDTO.getLiquidacionId()));
        }

        if (detalleLiquidacionRequestDTO.getOperadorId() != null) {
            detalleLiquidacion.setOperador(operadorRepository.getReferenceById(detalleLiquidacionRequestDTO.getOperadorId()));
        }

        if (detalleLiquidacionRequestDTO.getProveedorId() != null) {
            detalleLiquidacion.setProveedor(proveedorRepository.getReferenceById(detalleLiquidacionRequestDTO.getProveedorId()));
        }

        if (detalleLiquidacionRequestDTO.getProductoId() != null) {
            detalleLiquidacion.setProducto(productoRepository.getReferenceById(detalleLiquidacionRequestDTO.getProductoId()));
        }

        if (detalleLiquidacionRequestDTO.getViajeroId() != null) {
            detalleLiquidacion.setViajero(viajeroRepository.getReferenceById(detalleLiquidacionRequestDTO.getViajeroId()));
        }

        return detalleLiquidacionMapper.toResponseDTO(detalleLiquidacionRepository.save(detalleLiquidacion));
    }

    @Override
    public DetalleLiquidacionResponseDTO update(Integer id, DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacion detalleLiquidacion = detalleLiquidacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id));
            
        detalleLiquidacionMapper.updateEntityFromDTO(detalleLiquidacionRequestDTO, detalleLiquidacion);

        if(detalleLiquidacionRequestDTO.getLiquidacionId() != null) {
            detalleLiquidacion.setLiquidacion(liquidacionRepository.getReferenceById(detalleLiquidacionRequestDTO.getLiquidacionId()));
        }

        if (detalleLiquidacionRequestDTO.getOperadorId() != null) {
            detalleLiquidacion.setOperador(operadorRepository.getReferenceById(detalleLiquidacionRequestDTO.getOperadorId()));
        }

        if (detalleLiquidacionRequestDTO.getProveedorId() != null) {
            detalleLiquidacion.setProveedor(proveedorRepository.getReferenceById(detalleLiquidacionRequestDTO.getProveedorId()));
        }

        if (detalleLiquidacionRequestDTO.getProductoId() != null) {
            detalleLiquidacion.setProducto(productoRepository.getReferenceById(detalleLiquidacionRequestDTO.getProductoId()));
        }

        if (detalleLiquidacionRequestDTO.getViajeroId() != null) {
            detalleLiquidacion.setViajero(viajeroRepository.getReferenceById(detalleLiquidacionRequestDTO.getViajeroId()));
        }

        return detalleLiquidacionMapper.toResponseDTO(detalleLiquidacionRepository.save(detalleLiquidacion));
    }

    @Override
    public void deleteById(Integer id) {
        if (!detalleLiquidacionRepository.existsById(id)) 
            throw new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id);
        detalleLiquidacionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void saveBatch(Integer liquidacionId, List<DetalleLiquidacionRequestDTO> requestDTOs) {
        List<DetalleLiquidacion> entidadesParaGuardar = new java.util.ArrayList<>();
        
        for (DetalleLiquidacionRequestDTO dto : requestDTOs) {
            dto.setLiquidacionId(liquidacionId);
            DetalleLiquidacion detalleLiquidacion;
            
            if (dto.getId() != null) {
                detalleLiquidacion = detalleLiquidacionRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado"));
                detalleLiquidacionMapper.updateEntityFromDTO(dto, detalleLiquidacion);
            } else {
                detalleLiquidacion = detalleLiquidacionMapper.toEntity(dto);
            }
            
            if(dto.getLiquidacionId() != null) {
                detalleLiquidacion.setLiquidacion(liquidacionRepository.getReferenceById(dto.getLiquidacionId()));
            }
            if (dto.getOperadorId() != null) {
                detalleLiquidacion.setOperador(operadorRepository.getReferenceById(dto.getOperadorId()));
            }
            if (dto.getProveedorId() != null) {
                detalleLiquidacion.setProveedor(proveedorRepository.getReferenceById(dto.getProveedorId()));
            }
            if (dto.getProductoId() != null) {
                detalleLiquidacion.setProducto(productoRepository.getReferenceById(dto.getProductoId()));
            }
            if (dto.getViajeroId() != null) {
                detalleLiquidacion.setViajero(viajeroRepository.getReferenceById(dto.getViajeroId()));
            }
            
            entidadesParaGuardar.add(detalleLiquidacion);
        }
        
        detalleLiquidacionRepository.saveAll(entidadesParaGuardar);
    }

    private List<DetalleLiquidacionResponseDTO> mapToResponseList(List<DetalleLiquidacion> detalles) {
        return detalles.stream().map(detalleLiquidacionMapper::toResponseDTO).toList();
    }

    private List<DetalleLiquidacionSinLiquidacionDTO> mapToSinLiquidacionList(List<DetalleLiquidacion> detalles) {
        return detalles.stream().map(detalleLiquidacionMapper::toSinLiquidacionDTO).toList();
    }
}