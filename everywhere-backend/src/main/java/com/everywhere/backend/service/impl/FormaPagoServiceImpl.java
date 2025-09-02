package com.everywhere.backend.service.impl;

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

    @Override
    public List<FormaPagoResponseDTO> findAll() {
        return formaPagoRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FormaPagoResponseDTO findById(Integer id) {
        FormaPago formaPago = formaPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id));
        return convertToResponseDTO(formaPago);
    }

    @Override
    public FormaPagoResponseDTO findByCodigo(Integer codigo) {
        FormaPago formaPago = formaPagoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con código: " + codigo));
        return convertToResponseDTO(formaPago);
    }

    @Override
    public List<FormaPagoResponseDTO> findByDescripcion(String descripcion) {
        List<FormaPago> formasPago = formaPagoRepository.findByDescripcionContainingIgnoreCase(descripcion);
        return formasPago.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FormaPagoResponseDTO save(FormaPagoRequestDTO formaPagoRequestDTO) {
        if (formaPagoRepository.existsByCodigo(formaPagoRequestDTO.getCodigo())) {
            throw new BadRequestException("Ya existe una forma de pago con el código: " + formaPagoRequestDTO.getCodigo());
        }

        FormaPago formaPago = convertRequestToEntity(formaPagoRequestDTO);
        formaPago = formaPagoRepository.save(formaPago);
        return convertToResponseDTO(formaPago);
    }

    @Override
    public FormaPagoResponseDTO update(Integer id, FormaPagoRequestDTO formaPagoRequestDTO) {
        FormaPago existingFormaPago = formaPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id));

        // Solo actualizar el código si se envía y es diferente
        if (formaPagoRequestDTO.getCodigo() != null &&
            !existingFormaPago.getCodigo().equals(formaPagoRequestDTO.getCodigo())) {

            if (formaPagoRepository.existsByCodigo(formaPagoRequestDTO.getCodigo())) {
                throw new BadRequestException("Ya existe una forma de pago con el código: " + formaPagoRequestDTO.getCodigo());
            }
            existingFormaPago.setCodigo(formaPagoRequestDTO.getCodigo());
        }

        // Solo actualizar la descripción si se envía
        if (formaPagoRequestDTO.getDescripcion() != null &&
            !formaPagoRequestDTO.getDescripcion().trim().isEmpty()) {
            existingFormaPago.setDescripcion(formaPagoRequestDTO.getDescripcion());
        }

        existingFormaPago = formaPagoRepository.save(existingFormaPago);
        return convertToResponseDTO(existingFormaPago);
    }

    @Override
    public void deleteById(Integer id) {
        if (!formaPagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Forma de pago no encontrada con ID: " + id);
        }
        formaPagoRepository.deleteById(id);
    }

    private FormaPagoResponseDTO convertToResponseDTO(FormaPago formaPago) {
        FormaPagoResponseDTO dto = new FormaPagoResponseDTO();
        dto.setId(formaPago.getId());
        dto.setCodigo(formaPago.getCodigo());
        dto.setDescripcion(formaPago.getDescripcion());
        dto.setFechaCreacion(formaPago.getFechaCreacion());
        dto.setFechaActualizacion(formaPago.getFechaActualizacion());
        return dto;
    }

    private FormaPago convertRequestToEntity(FormaPagoRequestDTO dto) {
        FormaPago entity = new FormaPago();
        entity.setCodigo(dto.getCodigo());
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }
}
