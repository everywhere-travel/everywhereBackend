package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.DetalleDocumentoCobranzaMapper;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaRequestDTO;
import com.everywhere.backend.model.dto.DetalleDocumentoCobranzaResponseDTO;
import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import com.everywhere.backend.repository.DetalleDocumentoCobranzaRepository;
import com.everywhere.backend.repository.DocumentoCobranzaRepository;
import com.everywhere.backend.repository.ProductoRepository;
import com.everywhere.backend.service.DetalleDocumentoCobranzaService;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "detallesDocumentoCobranza")
public class DetalleDocumentoCobranzaServiceImpl implements DetalleDocumentoCobranzaService {

    private final DetalleDocumentoCobranzaRepository detalleDocumentoCobranzaRepository;
    private final DocumentoCobranzaRepository documentoCobranzaRepository;
    private final ProductoRepository productoRepository;
    private final DetalleDocumentoCobranzaMapper detalleDocumentoCobranzaMapper;

    @Override
    @Cacheable
    public List<DetalleDocumentoCobranzaResponseDTO> findAll() {
        return mapToResponseList(detalleDocumentoCobranzaRepository.findAllWithRelations());
    }

    @Override
    @Cacheable(value = "detalleDocumentoCobranzaById", key = "#id")
    public DetalleDocumentoCobranzaResponseDTO findById(Long id) {
        DetalleDocumentoCobranza detalle = detalleDocumentoCobranzaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle no encontrado con ID: " + id));
        return detalleDocumentoCobranzaMapper.toResponseDTO(detalle);
    }

    @Override
    @Cacheable(value = "detallesDocumentoCobranzaPorDocumentoId", key = "#documentoId")
    public List<DetalleDocumentoCobranzaResponseDTO> findByDocumentoCobranzaId(Long documentoId) {
        if (!documentoCobranzaRepository.existsById(documentoId))
            throw new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + documentoId);
        return mapToResponseList(detalleDocumentoCobranzaRepository.findByDocumentoCobranzaIdWithRelations(documentoId));
    }

    @Override
    @Transactional
    @CachePut(value = "detalleDocumentoCobranzaById", key = "#result.id")
    @CacheEvict(
        value = { 
            "detallesDocumentoCobranza", "detallesDocumentoCobranzaPorDocumentoId",
            "documentosCobranza", "documentoCobranzaById", "documentoCobranzaByNumero", "documentoCobranzaByCotizacionId"
        },
        allEntries = true
    )
    public DetalleDocumentoCobranzaResponseDTO save(DetalleDocumentoCobranzaRequestDTO detalleDocumentoCobranzaRequestDTO) {
        if (!documentoCobranzaRepository.existsById(detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId()))
            throw new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId());
        
        if (!productoRepository.existsById(detalleDocumentoCobranzaRequestDTO.getProductoId()))
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + detalleDocumentoCobranzaRequestDTO.getProductoId());

        DetalleDocumentoCobranza detalleDocumentoCobranza = detalleDocumentoCobranzaMapper.toEntity(detalleDocumentoCobranzaRequestDTO);
        detalleDocumentoCobranza.setDocumentoCobranza(documentoCobranzaRepository.findById(detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId()).get());
        detalleDocumentoCobranza.setProducto(productoRepository.findById(detalleDocumentoCobranzaRequestDTO.getProductoId()).get());
 
        return detalleDocumentoCobranzaMapper.toResponseDTO(detalleDocumentoCobranzaRepository.save(detalleDocumentoCobranza));
    }

    @Override
    @Transactional
    @CachePut(value = "detalleDocumentoCobranzaById", key = "#id")
    @CacheEvict(
        value = {
            "detallesDocumentoCobranza", "detallesDocumentoCobranzaPorDocumentoId",
            "documentosCobranza", "documentoCobranzaById", "documentoCobranzaByNumero", "documentoCobranzaByCotizacionId"
        },
        allEntries = true
    )
    public DetalleDocumentoCobranzaResponseDTO patch(Long id, DetalleDocumentoCobranzaRequestDTO detalleDocumentoCobranzaRequestDTO) {
        if (!detalleDocumentoCobranzaRepository.existsById(id))
            throw new ResourceNotFoundException("Detalle no encontrado con ID: " + id);

        DetalleDocumentoCobranza detalleDocumentoCobranza = detalleDocumentoCobranzaRepository.findById(id).get();
        detalleDocumentoCobranzaMapper.updateEntityFromRequest(detalleDocumentoCobranza, detalleDocumentoCobranzaRequestDTO);

        if (detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId() != null) {
            if (!documentoCobranzaRepository.existsById(detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId()))
                throw new ResourceNotFoundException("Documento de cobranza no encontrado con ID: " + detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId());
            detalleDocumentoCobranza.setDocumentoCobranza(documentoCobranzaRepository.findById(detalleDocumentoCobranzaRequestDTO.getDocumentoCobranzaId()).get());
        }

        if (detalleDocumentoCobranzaRequestDTO.getProductoId() != null) {
            if (!productoRepository.existsById(detalleDocumentoCobranzaRequestDTO.getProductoId()))
                throw new ResourceNotFoundException("Producto no encontrado con ID: " + detalleDocumentoCobranzaRequestDTO.getProductoId());
            detalleDocumentoCobranza.setProducto(productoRepository.findById(detalleDocumentoCobranzaRequestDTO.getProductoId()).get());
        }
 
        return detalleDocumentoCobranzaMapper.toResponseDTO(detalleDocumentoCobranzaRepository.save(detalleDocumentoCobranza));
    }

    @Override
    @Transactional
    @CacheEvict(
        value = { 
            "detallesDocumentoCobranza", "detalleDocumentoCobranzaById", "detallesDocumentoCobranzaPorDocumentoId",
            "documentosCobranza", "documentoCobranzaById", "documentoCobranzaByNumero", "documentoCobranzaByCotizacionId"
        }, 
        allEntries = true
    )
    public void deleteById(Long id) {
        if (!detalleDocumentoCobranzaRepository.existsById(id)) 
            throw new ResourceNotFoundException("Detalle no encontrado con ID: " + id);
        detalleDocumentoCobranzaRepository.deleteById(id);
    }

    private List<DetalleDocumentoCobranzaResponseDTO> mapToResponseList(List<DetalleDocumentoCobranza> detalles) {
        return detalles.stream().map(detalleDocumentoCobranzaMapper::toResponseDTO).toList();
    }
}