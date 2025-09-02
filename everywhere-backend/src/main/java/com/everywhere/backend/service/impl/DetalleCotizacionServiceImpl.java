package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.DetalleCotizacionMapper;
import com.everywhere.backend.model.dto.DetalleCotizacionRequestDto;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDto;
import com.everywhere.backend.model.entity.Cotizacion;
import com.everywhere.backend.model.entity.DetalleCotizacion;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.repository.CotizacionRepository;
import com.everywhere.backend.repository.DetalleCotizacionRepository;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.repository.ProveedorRepository;
import com.everywhere.backend.service.DetalleCotizacionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DetalleCotizacionServiceImpl implements DetalleCotizacionService {

    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final CotizacionRepository cotizacionRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;

    public DetalleCotizacionServiceImpl(
            DetalleCotizacionRepository detalleCotizacionRepository,
            CotizacionRepository cotizacionRepository,
            ProductoRepository productoRepository,
            ProveedorRepository proveedorRepository
    ) {
        this.detalleCotizacionRepository = detalleCotizacionRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public List<DetalleCotizacionResponseDto> findAll() {
        return detalleCotizacionRepository.findAll()
                .stream()
                .map(DetalleCotizacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DetalleCotizacionResponseDto> findById(int id) {
        return detalleCotizacionRepository.findById(id)
                .map(DetalleCotizacionMapper::toResponse);
    }

    @Override
    public List<DetalleCotizacionResponseDto> findByCotizacionId(int cotizacionId) {
        return detalleCotizacionRepository.findByCotizacionId(cotizacionId)
                .stream()
                .map(DetalleCotizacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DetalleCotizacionResponseDto create(DetalleCotizacionRequestDto dto, int cotizacionId) {
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new EntityNotFoundException("Cotización no encontrada"));

        DetalleCotizacion entity = DetalleCotizacionMapper.toEntity(dto);
        entity.setCotizacion(cotizacion);
        entity.setCreado(LocalDateTime.now());

        DetalleCotizacion saved = detalleCotizacionRepository.save(entity);
        return DetalleCotizacionMapper.toResponse(saved);
    }

    @Override
    public DetalleCotizacionResponseDto update(int id, DetalleCotizacionRequestDto dto) {
        DetalleCotizacion entity = detalleCotizacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detalle de cotización no encontrado"));

        DetalleCotizacionMapper.updateEntityFromRequest(entity, dto);
        entity.setActualizado(LocalDateTime.now());

        DetalleCotizacion updated = detalleCotizacionRepository.save(entity);
        return DetalleCotizacionMapper.toResponse(updated);
    }

    @Override
    public void delete(int id) {
        if (!detalleCotizacionRepository.existsById(id)) {
            throw new EntityNotFoundException("Detalle de cotización no encontrado");
        }
        detalleCotizacionRepository.deleteById(id);
    }

    @Override
    public DetalleCotizacionResponseDto setCotizacion(int detalleId, int cotizacionId) {
        DetalleCotizacion detalle = detalleCotizacionRepository.findById(detalleId)
                .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado"));
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new EntityNotFoundException("Cotización no encontrada"));

        detalle.setCotizacion(cotizacion);
        detalle.setActualizado(LocalDateTime.now());

        return DetalleCotizacionMapper.toResponse(detalleCotizacionRepository.save(detalle));
    }

    @Override
    public DetalleCotizacionResponseDto setProducto(int detalleId, int productoId) {
        DetalleCotizacion detalle = detalleCotizacionRepository.findById(detalleId)
                .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado"));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        detalle.setProducto(producto);
        detalle.setActualizado(LocalDateTime.now());

        return DetalleCotizacionMapper.toResponse(detalleCotizacionRepository.save(detalle));
    }

    @Override
    public DetalleCotizacionResponseDto setProveedor(int detalleId, int proveedorId) {
        DetalleCotizacion detalle = detalleCotizacionRepository.findById(detalleId)
                .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado"));
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        detalle.setProveedor(proveedor);
        detalle.setActualizado(LocalDateTime.now());

        return DetalleCotizacionMapper.toResponse(detalleCotizacionRepository.save(detalle));
    }
}
