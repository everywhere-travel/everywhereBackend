package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaRequestDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import com.everywhere.backend.model.entity.DocumentoCobranza;
import com.everywhere.backend.model.entity.Producto;
import com.everywhere.backend.repository.DetalleDocumentoCobranzaRepository;
import com.everywhere.backend.repository.DocumentoCobranzaRepository;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.service.DetalleDocumentoCobranzaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DetalleDocumentoCobranzaServiceImpl implements DetalleDocumentoCobranzaService {

    private final DetalleDocumentoCobranzaRepository detalleRepository;
    private final DocumentoCobranzaRepository documentoRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DetalleDocumentoCobranzaResponseDTO> findAll() {
        return detalleRepository.findAllWithRelations().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DetalleDocumentoCobranzaResponseDTO findById(Long id) {
        DetalleDocumentoCobranza detalle = detalleRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + id));
        return toResponseDTO(detalle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleDocumentoCobranzaResponseDTO> findByDocumentoCobranzaId(Long documentoId) {
        return detalleRepository.findByDocumentoCobranzaIdWithRelations(documentoId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DetalleDocumentoCobranzaResponseDTO save(DetalleDocumentoCobranzaRequestDTO dto) {
        // Buscar entidades necesarias
        DocumentoCobranza documento = documentoRepository.findById(dto.getDocumentoCobranzaId())
                .orElseThrow(() -> new ResourceNotFoundException("Documento de cobranza no encontrado"));
        
        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        // Crear entidad
        DetalleDocumentoCobranza detalle = new DetalleDocumentoCobranza();
        detalle.setCantidad(dto.getCantidad());
        detalle.setDescripcion(dto.getDescripcion());
        detalle.setPrecio(dto.getPrecio());
        detalle.setFechaCreacion(LocalDateTime.now());
        detalle.setDocumentoCobranza(documento);
        detalle.setProducto(producto);

        // Guardar
        DetalleDocumentoCobranza saved = detalleRepository.save(detalle);
        
        // Cargar con relaciones para respuesta
        return detalleRepository.findByIdWithRelations(saved.getId())
                .map(this::toResponseDTO)
                .orElse(toResponseDTO(saved));
    }

    @Override
    public DetalleDocumentoCobranzaResponseDTO update(Long id, DetalleDocumentoCobranzaRequestDTO dto) {
        DetalleDocumentoCobranza detalle = detalleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + id));

        // Actualizar SOLO los campos que vienen en el DTO (no nulos)
        if (dto.getCantidad() != null) {
            detalle.setCantidad(dto.getCantidad());
        }
        
        if (dto.getDescripcion() != null) {
            detalle.setDescripcion(dto.getDescripcion());
        }
        
        if (dto.getPrecio() != null) {
            detalle.setPrecio(dto.getPrecio());
        }
        
        if (dto.getDocumentoCobranzaId() != null) {
            DocumentoCobranza documento = documentoRepository.findById(dto.getDocumentoCobranzaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Documento de cobranza no encontrado"));
            detalle.setDocumentoCobranza(documento);
        }
        
        if (dto.getProductoId() != null) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            detalle.setProducto(producto);
        }

        DetalleDocumentoCobranza updated = detalleRepository.save(detalle);
        
        // Cargar con relaciones para respuesta
        return detalleRepository.findByIdWithRelations(updated.getId())
                .map(this::toResponseDTO)
                .orElse(toResponseDTO(updated));
    }

    @Override
    public void deleteById(Long id) {
        if (!detalleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Detalle no encontrado con ID: " + id);
        }
        detalleRepository.deleteById(id);
    }

    private DetalleDocumentoCobranzaResponseDTO toResponseDTO(DetalleDocumentoCobranza detalle) {
        DetalleDocumentoCobranzaResponseDTO dto = new DetalleDocumentoCobranzaResponseDTO();
        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setDescripcion(detalle.getDescripcion());
        dto.setPrecio(detalle.getPrecio());
        dto.setFechaCreacion(detalle.getFechaCreacion());
        
        // Mapear relaciones sin lazy loading
        if (detalle.getDocumentoCobranza() != null) {
            dto.setDocumentoCobranzaId(detalle.getDocumentoCobranza().getId());
            dto.setDocumentoCobranzaNumero(detalle.getDocumentoCobranza().getNumero());
        }
        
        if (detalle.getProducto() != null) {
            dto.setProductoId(detalle.getProducto().getId());
            dto.setProductoDescripcion(detalle.getProducto().getDescripcion());
        }
        
        return dto;
    }
}