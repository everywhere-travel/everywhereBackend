package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.DetalleCotizacionMapper;
import com.everywhere.backend.model.dto.DetalleCotizacionRequestDto;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.model.entity.*;
import com.everywhere.backend.repository.*;
import com.everywhere.backend.service.DetalleCotizacionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;  

@Service
@RequiredArgsConstructor
public class DetalleCotizacionServiceImpl implements DetalleCotizacionService {

    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final CotizacionRepository cotizacionRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final DetalleCotizacionMapper detalleCotizacionMapper;

    @Override
    public List<DetalleCotizacionResponseDto> findAll() {
        return detalleCotizacionRepository.findAll()
                .stream().map(detalleCotizacionMapper::toResponse).toList();
    }

    @Override
    public DetalleCotizacionResponseDto findById(Integer id) {
        return detalleCotizacionRepository.findById(id).map(detalleCotizacionMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Detalle de cotización no encontrado con ID: " + id));
    }

    @Override
    public List<DetalleCotizacionResponseDto> findByCotizacionId(Integer cotizacionId) {
        if (!cotizacionRepository.existsById(cotizacionId))
            throw new EntityNotFoundException("Cotización no encontrada con ID: " + cotizacionId);
        
        return detalleCotizacionRepository.findByCotizacionId(cotizacionId)
            .stream().map(detalleCotizacionMapper::toResponse).toList();
    }

    @Override
    public DetalleCotizacionResponseDto create(DetalleCotizacionRequestDto detalleCotizacionRequestDto, Integer cotizacionId) {
        
        DetalleCotizacion detalleCotizacion = detalleCotizacionMapper.toEntity(detalleCotizacionRequestDto);

        if (cotizacionId == null) throw new IllegalArgumentException("El ID de la cotización es obligatorio");
        
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + cotizacionId));
        detalleCotizacion.setCotizacion(cotizacion);

        if (detalleCotizacionRequestDto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(detalleCotizacionRequestDto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + detalleCotizacionRequestDto.getCategoriaId()));
            detalleCotizacion.setCategoria(categoria);
        }

        if (detalleCotizacionRequestDto.getProductoId() != null) {
            Producto producto = productoRepository.findById(detalleCotizacionRequestDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalleCotizacionRequestDto.getProductoId()));
            detalleCotizacion.setProducto(producto);
        }

        if (detalleCotizacionRequestDto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(detalleCotizacionRequestDto.getProveedorId())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + detalleCotizacionRequestDto.getProveedorId()));
            detalleCotizacion.setProveedor(proveedor);
        }

        return detalleCotizacionMapper.toResponse(detalleCotizacionRepository.save(detalleCotizacion));
    }

    @Override
    public DetalleCotizacionResponseDto patch(Integer id, DetalleCotizacionRequestDto detalleCotizacionRequestDto) {
        DetalleCotizacion detalleCotizacion = detalleCotizacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detalle de cotización no encontrado con ID: " + id));

        detalleCotizacionMapper.updateEntityFromRequest(detalleCotizacion, detalleCotizacionRequestDto);

        if (detalleCotizacionRequestDto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(detalleCotizacionRequestDto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            detalleCotizacion.setCategoria(categoria);
        }

        if (detalleCotizacionRequestDto.getProductoId() != null) {
            Producto producto = productoRepository.findById(detalleCotizacionRequestDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            detalleCotizacion.setProducto(producto);
        }

        if (detalleCotizacionRequestDto.getProveedorId() != null) {
            Proveedor proveedor = proveedorRepository.findById(detalleCotizacionRequestDto.getProveedorId())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
            detalleCotizacion.setProveedor(proveedor);
        }

        return detalleCotizacionMapper.toResponse(detalleCotizacionRepository.save(detalleCotizacion));
    }

    @Override
    public void delete(Integer id) {
        if (!detalleCotizacionRepository.existsById(id))
            throw new EntityNotFoundException("Detalle de cotización no encontrado con ID: " + id);
        detalleCotizacionRepository.deleteById(id);
    }
}