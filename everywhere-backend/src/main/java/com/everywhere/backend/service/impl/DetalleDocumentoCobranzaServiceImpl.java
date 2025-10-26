package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DetalleDocumentoCobranzaMapper;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetalleDocumentoCobranzaServiceImpl implements DetalleDocumentoCobranzaService {

    private final DetalleDocumentoCobranzaRepository detalleDocumentoCobranzaRepository;
    private final DocumentoCobranzaRepository documentoCobranzaRepository;
    private final ProductoRepository productoRepository;
    private final DetalleDocumentoCobranzaMapper detalleDocumentoCobranzaMapper;

    @Override
    public List<DetalleDocumentoCobranzaResponseDTO> findAll() {
        return detalleDocumentoCobranzaRepository.findAllWithRelations().stream()
                .map(detalleDocumentoCobranzaMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public DetalleDocumentoCobranzaResponseDTO findById(Long id) {
        DetalleDocumentoCobranza detalle = detalleDocumentoCobranzaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + id));
        return detalleDocumentoCobranzaMapper.toResponseDTO(detalle);
    }

    @Override
    public List<DetalleDocumentoCobranzaResponseDTO> findByDocumentoCobranzaId(Long documentoId) {
        return detalleDocumentoCobranzaRepository.findByDocumentoCobranzaIdWithRelations(documentoId).stream()
                .map(detalleDocumentoCobranzaMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DetalleDocumentoCobranzaResponseDTO save(DetalleDocumentoCobranzaRequestDTO detalleDocumentoCobranzaRequestDTO) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findById(detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId())
                .orElseThrow(() -> new ResourceNotFoundException("Documento de cobranza no encontrado"));
        
        Producto producto = productoRepository.findById(detalleDocumentoCobranzaRequestDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        DetalleDocumentoCobranza detalleDocumentoCobranza = detalleDocumentoCobranzaMapper.toEntity(detalleDocumentoCobranzaRequestDTO);
        detalleDocumentoCobranza.setDocumentoCobranza(documentoCobranza);
        detalleDocumentoCobranza.setProducto(producto);

        detalleDocumentoCobranza = detalleDocumentoCobranzaRepository.save(detalleDocumentoCobranza);
        return detalleDocumentoCobranzaMapper.toResponseDTO(detalleDocumentoCobranza);
    }

    @Override
    @Transactional
    public DetalleDocumentoCobranzaResponseDTO update(Long id, DetalleDocumentoCobranzaRequestDTO dto) {
        DetalleDocumentoCobranza detalleDocumentoCobranza = detalleDocumentoCobranzaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + id));

        // Actualizar usando detalleDocumentoCobranzaMapper
        detalleDocumentoCobranzaMapper.updateEntityFromRequest(detalleDocumentoCobranza, dto);

        if (dto.getDocumentoCobranzaId() != null) {
            DocumentoCobranza documento = documentoCobranzaRepository.findById(dto.getDocumentoCobranzaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Documento de cobranza no encontrado"));
            detalleDocumentoCobranza.setDocumentoCobranza(documento);
        }
        
        if (dto.getProductoId() != null) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            detalleDocumentoCobranza.setProducto(producto);
        }

        detalleDocumentoCobranza = detalleDocumentoCobranzaRepository.save(detalleDocumentoCobranza);
        return detalleDocumentoCobranzaMapper.toResponseDTO(detalleDocumentoCobranza);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!detalleDocumentoCobranzaRepository.existsById(id)) throw new ResourceNotFoundException("Detalle no encontrado con ID: " + id);
        detalleDocumentoCobranzaRepository.deleteById(id);
    }
}