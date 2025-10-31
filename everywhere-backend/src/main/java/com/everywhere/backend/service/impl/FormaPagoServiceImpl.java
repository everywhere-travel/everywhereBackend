package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.FormaPagoMapper;
import com.everywhere.backend.model.dto.FormaPagoRequestDTO;
import com.everywhere.backend.model.dto.FormaPagoResponseDTO;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.service.FormaPagoService;

import lombok.RequiredArgsConstructor;

import com.everywhere.backend.exceptions.ResourceNotFoundException; 
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormaPagoServiceImpl implements FormaPagoService {

    private FormaPagoRepository formaPagoRepository;
    private FormaPagoMapper formaPagoMapper;

    @Override
    public List<FormaPagoResponseDTO> findAll() {
        return formaPagoRepository.findAll().stream().map(formaPagoMapper::toResponseDTO).toList();
    }

    @Override
    public FormaPagoResponseDTO findById(Integer id) {
        return formaPagoRepository.findById(id).map(formaPagoMapper::toResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id));
    }

    @Override
    public FormaPagoResponseDTO findByCodigo(Integer codigo) {
        return formaPagoRepository.findByCodigo(codigo).map(formaPagoMapper::toResponseDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con código: " + codigo));
    }

    @Override
    public List<FormaPagoResponseDTO> findByDescripcion(String descripcion) {
        return formaPagoRepository.findByDescripcionContainingIgnoreCase(descripcion)
            .stream().map(formaPagoMapper::toResponseDTO).toList();
    }

    @Override
    public FormaPagoResponseDTO save(FormaPagoRequestDTO formaPagoRequestDTO) {
        if (formaPagoRepository.existsByCodigo(formaPagoRequestDTO.getCodigo()))
            throw new DataIntegrityViolationException("Ya existe una forma de pago con el código: " + formaPagoRequestDTO.getCodigo());
        FormaPago formaPago = formaPagoMapper.toEntity(formaPagoRequestDTO);
        return formaPagoMapper.toResponseDTO(formaPagoRepository.save(formaPago));
    }

    @Override
    public FormaPagoResponseDTO update(Integer id, FormaPagoRequestDTO formaPagoRequestDTO) {
        FormaPago formaPago = formaPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id));

        if (formaPagoRequestDTO.getCodigo() != null) {
            if (formaPagoRepository.existsByCodigo(formaPagoRequestDTO.getCodigo()))
                throw new DataIntegrityViolationException("Ya existe una forma de pago con el código: " + formaPagoRequestDTO.getCodigo());
            formaPago.setCodigo(formaPagoRequestDTO.getCodigo());
        } 

        if (formaPagoRequestDTO.getDescripcion() != null && !formaPagoRequestDTO.getDescripcion().trim().isEmpty())
            formaPago.setDescripcion(formaPagoRequestDTO.getDescripcion());

        return formaPagoMapper.toResponseDTO(formaPagoRepository.save(formaPago));
    }

    @Override
    public void deleteById(Integer id) {
        if (!formaPagoRepository.existsById(id))
            throw new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id);
        formaPagoRepository.deleteById(id);
    }
}