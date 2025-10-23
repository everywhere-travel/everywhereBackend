package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.DetalleCotizacionMapper;
import com.everywhere.backend.model.dto.DetalleCotizacionRequestDTO;
import com.everywhere.backend.model.dto.DetalleCotizacionResponseDTO;
import com.everywhere.backend.model.entity.*;
import com.everywhere.backend.repository.*;
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
    private final CategoriaRepository categoriaRepository;
    private final OperadorRepository operadorRepository;
    private final LiquidacionRepository liquidacionRepository;
    private final ViajeroRepository viajeroRepository;

    public DetalleCotizacionServiceImpl(
            DetalleCotizacionRepository detalleCotizacionRepository,
            CotizacionRepository cotizacionRepository,
            ProductoRepository productoRepository,
            ProveedorRepository proveedorRepository,
            CategoriaRepository categoriaRepository,
            OperadorRepository operadorRepository,
            LiquidacionRepository liquidacionRepository,
            ViajeroRepository viajeroRepository
    ) {
        this.detalleCotizacionRepository = detalleCotizacionRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.categoriaRepository = categoriaRepository;
        this.operadorRepository = operadorRepository;
        this.liquidacionRepository = liquidacionRepository;
        this.viajeroRepository = viajeroRepository;
    }

    @Override
    public List<DetalleCotizacionResponseDTO> findAll() {
        return detalleCotizacionRepository.findAll()
                .stream()
                .map(DetalleCotizacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<DetalleCotizacionResponseDTO> findById(int id) {
        return detalleCotizacionRepository.findById(id)
                .map(DetalleCotizacionMapper::toResponse);
    }

    @Override
    public List<DetalleCotizacionResponseDTO> findByCotizacionId(int cotizacionId) {
        return detalleCotizacionRepository.findByCotizacionId(cotizacionId)
                .stream()
                .map(DetalleCotizacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DetalleCotizacionResponseDTO create(DetalleCotizacionRequestDTO dto, int cotizacionId) {
        DetalleCotizacion entity = new DetalleCotizacion();

        // Relación Cotización
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));
        entity.setCotizacion(cotizacion);

        // Relación Categoria
        Categoria categoria = categoriaRepository.findById(dto.getCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        entity.setCategoria(categoria);

        // Campos simples
        entity.setCantidad(dto.getCantidad());
        entity.setUnidad(dto.getUnidad());
        entity.setDescripcion(dto.getDescripcion());
        entity.setComision(dto.getComision());
        entity.setPrecioHistorico(dto.getPrecioHistorico());
        entity.setSeleccionado(dto.getSeleccionado()); // Campo opcional, puede ser null

        DetalleCotizacion saved = detalleCotizacionRepository.save(entity);
        return DetalleCotizacionMapper.toResponse(saved);
    }


    @Override
    public DetalleCotizacionResponseDTO update(int id, DetalleCotizacionRequestDTO dto) {
        DetalleCotizacion entity = detalleCotizacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Detalle de cotización no encontrado"));

    DetalleCotizacionMapper.updateEntityFromRequest(entity, dto, categoriaRepository);
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
    public DetalleCotizacionResponseDTO setCotizacion(int detalleId, int cotizacionId) {
        DetalleCotizacion detalle = detalleCotizacionRepository.findById(detalleId)
                .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado"));
        Cotizacion cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new EntityNotFoundException("Cotización no encontrada"));

        detalle.setCotizacion(cotizacion);
        detalle.setActualizado(LocalDateTime.now());

        return DetalleCotizacionMapper.toResponse(detalleCotizacionRepository.save(detalle));
    }

    @Override
    public DetalleCotizacionResponseDTO setProducto(int detalleId, int productoId) {
        DetalleCotizacion detalle = detalleCotizacionRepository.findById(detalleId)
                .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado"));
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));

        detalle.setProducto(producto);
        detalle.setActualizado(LocalDateTime.now());

        return DetalleCotizacionMapper.toResponse(detalleCotizacionRepository.save(detalle));
    }

    @Override
    public DetalleCotizacionResponseDTO setProveedor(int detalleId, int proveedorId) {
        DetalleCotizacion detalle = detalleCotizacionRepository.findById(detalleId)
                .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado"));
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado"));

        detalle.setProveedor(proveedor);
        detalle.setActualizado(LocalDateTime.now());

        return DetalleCotizacionMapper.toResponse(detalleCotizacionRepository.save(detalle));
    }

    @Override
    public DetalleCotizacionResponseDTO updateSeleccionado(int detalleId, Boolean seleccionado) {
        if (detalleId <= 0) {
            throw new IllegalArgumentException("El ID del detalle debe ser un número válido mayor a 0");
        }
        if (seleccionado == null) {
            throw new IllegalArgumentException("El valor de seleccionado no puede ser null");
        }
        
        DetalleCotizacion detalle = detalleCotizacionRepository.findById(detalleId)
                .orElseThrow(() -> new EntityNotFoundException("Detalle no encontrado con ID: " + detalleId));
        
        detalle.setSeleccionado(seleccionado);
        detalle.setActualizado(LocalDateTime.now());
        
        DetalleCotizacion saved = detalleCotizacionRepository.save(detalle);
        return DetalleCotizacionMapper.toResponse(saved);
    }

}
