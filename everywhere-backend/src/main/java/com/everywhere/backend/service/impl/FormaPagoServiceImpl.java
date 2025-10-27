package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.FormaPagoMapper;
import com.everywhere.backend.model.dto.FormaPagoRequestDTO;
import com.everywhere.backend.model.dto.FormaPagoResponseDTO;
import com.everywhere.backend.model.entity.FormaPago;
import com.everywhere.backend.repository.FormaPagoRepository;
import com.everywhere.backend.service.FormaPagoService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormaPagoServiceImpl implements FormaPagoService {

    @Autowired
    private FormaPagoRepository formaPagoRepository;

    @Autowired
    private FormaPagoMapper formaPagoMapper;

    @Override
    public List<FormaPagoResponseDTO> findAll() {
        return formaPagoRepository.findAll().stream()
                .map(formaPagoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FormaPagoResponseDTO findById(Integer id) {
        return formaPagoRepository.findById(id)
                .map(formaPagoMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id));
    }

    @Override
    public FormaPagoResponseDTO findByCodigo(Integer codigo) {
        return formaPagoRepository.findByCodigo(codigo)
                .map(formaPagoMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con código: " + codigo));
    }

    @Override
    public List<FormaPagoResponseDTO> findByDescripcion(String descripcion) {
        return formaPagoRepository.findByDescripcionContainingIgnoreCase(descripcion)
                .stream()
                .map(formaPagoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FormaPagoResponseDTO save(FormaPagoRequestDTO formaPagoRequestDTO) {
        if (formaPagoRepository.existsByCodigo(formaPagoRequestDTO.getCodigo())) {
            throw new BadRequestException("Ya existe una forma de pago con el código: " + formaPagoRequestDTO.getCodigo());
        }
        FormaPago entity = formaPagoMapper.toEntity(formaPagoRequestDTO);
        return formaPagoMapper.toResponseDTO(formaPagoRepository.save(entity));
    }

    @Override
    public FormaPagoResponseDTO update(Integer id, FormaPagoRequestDTO formaPagoRequestDTO) {
        FormaPago existing = formaPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id));

        if (formaPagoRequestDTO.getCodigo() != null &&
                !existing.getCodigo().equals(formaPagoRequestDTO.getCodigo())) {
            if (formaPagoRepository.existsByCodigo(formaPagoRequestDTO.getCodigo())) {
                throw new BadRequestException("Ya existe una forma de pago con el código: " + formaPagoRequestDTO.getCodigo());
            }
            existing.setCodigo(formaPagoRequestDTO.getCodigo());
        }

        if (formaPagoRequestDTO.getDescripcion() != null &&
                !formaPagoRequestDTO.getDescripcion().trim().isEmpty()) {
            existing.setDescripcion(formaPagoRequestDTO.getDescripcion());
        }

        return formaPagoMapper.toResponseDTO(formaPagoRepository.save(existing));
    }

    @Override
    public void deleteById(Integer id) {
        if (!formaPagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id);
        }
        formaPagoRepository.deleteById(id);
    }
}

