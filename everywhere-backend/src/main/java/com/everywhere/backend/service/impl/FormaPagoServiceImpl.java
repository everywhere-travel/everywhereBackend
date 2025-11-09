package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.FormaPagoMapper;
import com.everywhere.backend.model.dto.FormaPagoRequestDTO;
import com.everywhere.backend.model.dto.FormaPagoResponseDTO;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.service.FormaPagoService;

import lombok.RequiredArgsConstructor;

import com.everywhere.backend.exceptions.ResourceNotFoundException;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@CacheConfig(cacheNames = "formasPago")
public class FormaPagoServiceImpl implements FormaPagoService {

    private final FormaPagoRepository formaPagoRepository;
    private final FormaPagoMapper formaPagoMapper;

    @Override
    @Cacheable
    public List<FormaPagoResponseDTO> findAll() {
        return mapToResponseList(formaPagoRepository.findAll());
    }

    @Override
    @Cacheable(value = "formaPagoById", key = "#id")
    public FormaPagoResponseDTO findById(Integer id) {
        return formaPagoRepository.findById(id).map(formaPagoMapper::toResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id));
    }

    @Override
    @Cacheable(value = "formaPagoByCodigo", key = "#codigo")
    public FormaPagoResponseDTO findByCodigo(Integer codigo) {
        return formaPagoRepository.findByCodigo(codigo).map(formaPagoMapper::toResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con código: " + codigo));
    }

    @Override
    @Cacheable(value = "formasPagoByDescripcion", key = "#descripcion")
    public List<FormaPagoResponseDTO> findByDescripcion(String descripcion) {
        return mapToResponseList(formaPagoRepository.findByDescripcionContainingIgnoreCase(descripcion));
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "formasPago", "formaPagoById", "formaPagoByCodigo", "formasPagoByDescripcion",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion",
            "liquidaciones", "liquidacion", "liquidacionConDetalles"
        }, allEntries = true)
    public FormaPagoResponseDTO save(FormaPagoRequestDTO formaPagoRequestDTO) {
        if (formaPagoRepository.existsByCodigo(formaPagoRequestDTO.getCodigo()))
            throw new DataIntegrityViolationException("Ya existe una forma de pago con el código: " + formaPagoRequestDTO.getCodigo());
        FormaPago formaPago = formaPagoMapper.toEntity(formaPagoRequestDTO);
        return formaPagoMapper.toResponseDTO(formaPagoRepository.save(formaPago));
    }

    @Override
    @Transactional
    @CachePut(value = "formaPagoById", key = "#id")
    @CacheEvict(
        value = {
            "formasPago", "formaPagoByCodigo", "formasPagoByDescripcion",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion",
            "liquidaciones", "liquidacion", "liquidacionConDetalles"
        }, allEntries = true)
    public FormaPagoResponseDTO update(Integer id, FormaPagoRequestDTO formaPagoRequestDTO) {
        if (!formaPagoRepository.existsById(id))
            throw new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id);

        FormaPago formaPago = formaPagoRepository.findById(id).get();

        if (formaPagoRequestDTO.getCodigo() != null && 
            !formaPagoRequestDTO.getCodigo().equals(formaPago.getCodigo()) &&
            formaPagoRepository.existsByCodigo(formaPagoRequestDTO.getCodigo())) {
            throw new DataIntegrityViolationException("Ya existe una forma de pago con el código: " + formaPagoRequestDTO.getCodigo());
        }

        if (formaPagoRequestDTO.getCodigo() != null)
            formaPago.setCodigo(formaPagoRequestDTO.getCodigo());

        if (formaPagoRequestDTO.getDescripcion() != null && !formaPagoRequestDTO.getDescripcion().trim().isEmpty())
            formaPago.setDescripcion(formaPagoRequestDTO.getDescripcion());

        return formaPagoMapper.toResponseDTO(formaPagoRepository.save(formaPago));
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "formasPago", "formaPagoById", "formaPagoByCodigo", "formasPagoByDescripcion",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion",
            "liquidaciones", "liquidacion", "liquidacionConDetalles"
    }, allEntries = true)
    public void deleteById(Integer id) {
        if (!formaPagoRepository.existsById(id))
            throw new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id);
        formaPagoRepository.deleteById(id);
    }

    private List<FormaPagoResponseDTO> mapToResponseList(List<FormaPago> formasPago) {
        return formasPago.stream().map(formaPagoMapper::toResponseDTO).toList();
    }
}