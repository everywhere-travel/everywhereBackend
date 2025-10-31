package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.DetalleLiquidacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleLiquidacionResponseDTO;
import com.everywhere.backend.model.entity.DetalleLiquidacion;
import com.everywhere.backend.model.entity.Liquidacion;
import com.everywhere.backend.model.entity.Operador;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.model.entity.Viajero;
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
        return detalleLiquidacionRepository.findAllWithRelations().stream()
                .map(detalleLiquidacionMapper::toResponseDTO).toList();
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
        return detalles.stream().map(detalleLiquidacionMapper::toResponseDTO).toList();
    }

    @Override
    public DetalleLiquidacionResponseDTO save(DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacion detalleLiquidacion = detalleLiquidacionMapper.toEntity(detalleLiquidacionRequestDTO);

        if(detalleLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = liquidacionRepository.findById(detalleLiquidacionRequestDTO.getLiquidacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + detalleLiquidacionRequestDTO.getLiquidacionId()));
            detalleLiquidacion.setLiquidacion(liquidacion);
        }

        if (detalleLiquidacionRequestDTO.getOperadorId() != null) {
            Operador operador = operadorRepository.findById(detalleLiquidacionRequestDTO.getOperadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Operador no encontrado con ID: " + detalleLiquidacionRequestDTO.getOperadorId()));
            detalleLiquidacion.setOperador(operador);
        }

        if (detalleLiquidacionRequestDTO.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(detalleLiquidacionRequestDTO.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + detalleLiquidacionRequestDTO.getProveedorId()));
            detalleLiquidacion.setProveedor(proveedor);
        }

        if (detalleLiquidacionRequestDTO.getProductoId() != null) {
            Producto producto = productoRepository.findById(detalleLiquidacionRequestDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleLiquidacionRequestDTO.getProductoId()));
            detalleLiquidacion.setProducto(producto);
        }

        if (detalleLiquidacionRequestDTO.getViajeroId() != null) {
            Viajero viajero = viajeroRepository.findById(detalleLiquidacionRequestDTO.getViajeroId())
                    .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con ID: " + detalleLiquidacionRequestDTO.getViajeroId()));
            detalleLiquidacion.setViajero(viajero);
        }

        return detalleLiquidacionMapper.toResponseDTO(detalleLiquidacionRepository.save(detalleLiquidacion));
    }

    @Override
    public DetalleLiquidacionResponseDTO update(Integer id, DetalleLiquidacionRequestDTO detalleLiquidacionRequestDTO) {
        DetalleLiquidacion detalleLiquidacion = detalleLiquidacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id));

        detalleLiquidacionMapper.updateEntityFromDTO(detalleLiquidacionRequestDTO, detalleLiquidacion);

        if(detalleLiquidacionRequestDTO.getLiquidacionId() != null) {
            Liquidacion liquidacion = liquidacionRepository.findById(detalleLiquidacionRequestDTO.getLiquidacionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Liquidación no encontrada con ID: " + detalleLiquidacionRequestDTO.getLiquidacionId()));
            detalleLiquidacion.setLiquidacion(liquidacion);
        }

        if (detalleLiquidacionRequestDTO.getOperadorId() != null) {
            Operador operador = operadorRepository.findById(detalleLiquidacionRequestDTO.getOperadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Operador no encontrado con ID: " + detalleLiquidacionRequestDTO.getOperadorId()));
            detalleLiquidacion.setOperador(operador);
        }

        if (detalleLiquidacionRequestDTO.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(detalleLiquidacionRequestDTO.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + detalleLiquidacionRequestDTO.getProveedorId()));
            detalleLiquidacion.setProveedor(proveedor);
        }

        if (detalleLiquidacionRequestDTO.getProductoId() != null) {
            Producto producto = productoRepository.findById(detalleLiquidacionRequestDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleLiquidacionRequestDTO.getProductoId()));
            detalleLiquidacion.setProducto(producto);
        }

        if (detalleLiquidacionRequestDTO.getViajeroId() != null) {
            Viajero viajero = viajeroRepository.findById(detalleLiquidacionRequestDTO.getViajeroId())
                    .orElseThrow(() -> new ResourceNotFoundException("Viajero no encontrado con ID: " + detalleLiquidacionRequestDTO.getViajeroId()));
            detalleLiquidacion.setViajero(viajero);
        }

        return detalleLiquidacionMapper.toResponseDTO(detalleLiquidacionRepository.save(detalleLiquidacion));
    }

    @Override
    public void deleteById(Integer id) {
        if (!detalleLiquidacionRepository.existsById(id)) 
            throw new ResourceNotFoundException("Detalle de liquidación no encontrado con ID: " + id);
        detalleLiquidacionRepository.deleteById(id);
    }
}