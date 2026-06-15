package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.FormaPagoRequestDTO;
import com.everywhere.backend.model.dto.FormaPagoResponseDTO;
import com.everywhere.backend.model.dto.DropdownResponseDTO;

import java.util.List;

public interface FormaPagoService {
    List<FormaPagoResponseDTO> findAll();
    FormaPagoResponseDTO findById(Integer id);
    FormaPagoResponseDTO findByCodigo(Integer codigo);
    List<FormaPagoResponseDTO> findByDescripcionContaining(String descripcion);
    FormaPagoResponseDTO save(FormaPagoRequestDTO formaPagoRequestDTO);
    FormaPagoResponseDTO patch(Integer id, FormaPagoRequestDTO formaPagoRequestDTO);
    void deleteById(Integer id);

    List<DropdownResponseDTO> getDropdown();
}
