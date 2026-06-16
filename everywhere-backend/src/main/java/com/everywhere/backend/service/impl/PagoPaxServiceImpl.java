package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.PagoPaxMapper;
import com.everywhere.backend.model.dto.PagoPaxRequestDTO;
import com.everywhere.backend.model.dto.PagoPaxResponseDTO;
import com.everywhere.backend.model.entity.PagoPax;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.repository.LiquidacionRepository;
import com.everywhere.backend.repository.PagoPaxRepository;
import com.everywhere.backend.repository.ProveedorRepository;
import com.everywhere.backend.service.PagoPaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.everywhere.backend.service.AsientoContableService;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PagoPaxServiceImpl implements PagoPaxService {

    private final PagoPaxRepository pagoPaxRepository;
    private final LiquidacionRepository liquidacionRepository;
    private final FormaPagoRepository formaPagoRepository;
    private final ProveedorRepository proveedorRepository;
    private final PagoPaxMapper pagoPaxMapper;
    private final AsientoContableService asientoContableService;

    @Override
    @Transactional
    public PagoPaxResponseDTO create(PagoPaxRequestDTO requestDTO) {
        // Crear la entidad
        PagoPax pagoPax = pagoPaxMapper.toEntity(requestDTO);
        
        if (requestDTO.getLiquidacionId() != null) {
            pagoPax.setLiquidacion(liquidacionRepository.getReferenceById(requestDTO.getLiquidacionId()));
        }
        if (requestDTO.getFormaPagoId() != null) {
            pagoPax.setFormaPago(formaPagoRepository.getReferenceById(requestDTO.getFormaPagoId()));
        }
        if (requestDTO.getProveedorId() != null) {
            pagoPax.setProveedor(proveedorRepository.getReferenceById(requestDTO.getProveedorId()));
        }

        // Guardar
        pagoPax = pagoPaxRepository.save(pagoPax);
        asientoContableService.generarAsientoPorPagoPax(pagoPax);

        return pagoPaxMapper.toResponseDTO(pagoPax);
    }

    @Override
    public PagoPaxResponseDTO findById(Integer id) {
        PagoPax pagoPax = pagoPaxRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago Pax no encontrado con ID: " + id));

        return pagoPaxMapper.toResponseDTO(pagoPax);
    }

    @Override
    public List<PagoPaxResponseDTO> findAll() {
        return pagoPaxRepository.findAllWithRelations().stream()
                .map(pagoPaxMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagoPaxResponseDTO> findByLiquidacionId(Integer liquidacionId) {
        // Validar que existe la liquidación
        if (!liquidacionRepository.existsById(liquidacionId)) {
            throw new ResourceNotFoundException("Liquidación no encontrada con ID: " + liquidacionId);
        }

        return pagoPaxRepository.findByLiquidacionId(liquidacionId).stream()
                .map(pagoPaxMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PagoPaxResponseDTO update(Integer id, PagoPaxRequestDTO requestDTO) {
        // Buscar el pago pax existente
        PagoPax pagoPax = pagoPaxRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago Pax no encontrado con ID: " + id));

        // Actualizar datos básicos
        pagoPaxMapper.updateEntityFromRequestDTO(pagoPax, requestDTO);

        // Actualizar liquidación si cambió
        if (requestDTO.getLiquidacionId() != null 
            && !requestDTO.getLiquidacionId().equals(pagoPax.getLiquidacion().getId())) {
            pagoPax.setLiquidacion(liquidacionRepository.getReferenceById(requestDTO.getLiquidacionId()));
        }

        // Actualizar forma de pago si cambió
        if (requestDTO.getFormaPagoId() != null 
            && !requestDTO.getFormaPagoId().equals(pagoPax.getFormaPago().getId())) {
            pagoPax.setFormaPago(formaPagoRepository.getReferenceById(requestDTO.getFormaPagoId()));
        }

        // Actualizar proveedor si cambió
        if (requestDTO.getProveedorId() != null 
            && (pagoPax.getProveedor() == null || !requestDTO.getProveedorId().equals(pagoPax.getProveedor().getId()))) {
            pagoPax.setProveedor(proveedorRepository.getReferenceById(requestDTO.getProveedorId()));
        }

        // Guardar
        pagoPax = pagoPaxRepository.save(pagoPax);
   

        return pagoPaxMapper.toResponseDTO(pagoPax);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!pagoPaxRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pago Pax no encontrado con ID: " + id);
        }

        pagoPaxRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void saveBatch(Integer liquidacionId, List<PagoPaxRequestDTO> requestDTOs) {
        List<PagoPax> pagosParaGuardar = new java.util.ArrayList<>();
        
        for (PagoPaxRequestDTO dto : requestDTOs) {
            dto.setLiquidacionId(liquidacionId);
            PagoPax pagoPax;
            
            if (dto.getId() != null) {
                pagoPax = pagoPaxRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pago Pax no encontrado"));
                pagoPaxMapper.updateEntityFromRequestDTO(pagoPax, dto);
            } else {
                pagoPax = pagoPaxMapper.toEntity(dto);
            }
            
            if (dto.getLiquidacionId() != null) {
                pagoPax.setLiquidacion(liquidacionRepository.getReferenceById(dto.getLiquidacionId()));
            }
            if (dto.getFormaPagoId() != null) {
                pagoPax.setFormaPago(formaPagoRepository.getReferenceById(dto.getFormaPagoId()));
            }
            if (dto.getProveedorId() != null) {
                pagoPax.setProveedor(proveedorRepository.getReferenceById(dto.getProveedorId()));
            }
            
            pagoPax = pagoPaxRepository.save(pagoPax);
            if (dto.getId() == null) {
                asientoContableService.generarAsientoPorPagoPax(pagoPax);
            }
            
            pagosParaGuardar.add(pagoPax);
        }
    }
}
