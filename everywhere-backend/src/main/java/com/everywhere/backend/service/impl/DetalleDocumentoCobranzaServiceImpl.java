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
                .map(detalleDocumentoCobranzaMapper::toResponseDTO).toList();
    }

    @Override
    public DetalleDocumentoCobranzaResponseDTO findById(Long id) {
        DetalleDocumentoCobranza detalle = detalleDocumentoCobranzaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + id));
        return detalleDocumentoCobranzaMapper.toResponseDTO(detalle);
    }

    @Override
    public List<DetalleDocumentoCobranzaResponseDTO> findByDocumentoCobranzaId(Long documentoId) {
        if (!documentoCobranzaRepository.existsById(documentoId))
            throw new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + documentoId);
        return detalleDocumentoCobranzaRepository.findByDocumentoCobranzaIdWithRelations(documentoId).stream()
                .map(detalleDocumentoCobranzaMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public DetalleDocumentoCobranzaResponseDTO save(DetalleDocumentoCobranzaRequestDTO detalleDocumentoCobranzaRequestDTO) {
        DocumentoCobranza documentoCobranza = documentoCobranzaRepository.findById(detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId())
                .orElseThrow(() -> new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId()));
        
        Producto producto = productoRepository.findById(detalleDocumentoCobranzaRequestDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleDocumentoCobranzaRequestDTO.getProductoId()));

        DetalleDocumentoCobranza detalleDocumentoCobranza = detalleDocumentoCobranzaMapper.toEntity(detalleDocumentoCobranzaRequestDTO);
        detalleDocumentoCobranza.setDocumentoCobranza(documentoCobranza);
        detalleDocumentoCobranza.setProducto(producto);
 
        return detalleDocumentoCobranzaMapper.toResponseDTO(detalleDocumentoCobranzaRepository.save(detalleDocumentoCobranza));
    }

    @Override
    @Transactional
    public DetalleDocumentoCobranzaResponseDTO patch(Long id, DetalleDocumentoCobranzaRequestDTO detalleDocumentoCobranzaRequestDTO) {
        DetalleDocumentoCobranza detalleDocumentoCobranza = detalleDocumentoCobranzaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + id));

        detalleDocumentoCobranzaMapper.updateEntityFromRequest(detalleDocumentoCobranza, detalleDocumentoCobranzaRequestDTO);

        if (detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId() != null) {
            DocumentoCobranza documento = documentoCobranzaRepository.findById(detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId()));
            detalleDocumentoCobranza.setDocumentoCobranza(documento);
        }

        if (detalleDocumentoCobranzaRequestDTO.getProductoId() != null) {
            Producto producto = productoRepository.findById(detalleDocumentoCobranzaRequestDTO.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalleDocumentoCobranzaRequestDTO.getProductoId()));
            detalleDocumentoCobranza.setProducto(producto);
        }
 
        return detalleDocumentoCobranzaMapper.toResponseDTO(detalleDocumentoCobranzaRepository.save(detalleDocumentoCobranza));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!detalleDocumentoCobranzaRepository.existsById(id)) 
            throw new ResourceNotFoundException("Detalle no encontrado con ID: " + id);
        detalleDocumentoCobranzaRepository.deleteById(id);
    }
}