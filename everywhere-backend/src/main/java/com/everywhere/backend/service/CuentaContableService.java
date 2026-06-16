package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CuentaContableRequestDTO;
import com.everywhere.backend.model.dto.CuentaContableResponseDTO;

import java.util.List;

public interface CuentaContableService {

    List<CuentaContableResponseDTO> listar();

    CuentaContableResponseDTO obtenerPorId(Integer id);

    CuentaContableResponseDTO crear(CuentaContableRequestDTO request);

    CuentaContableResponseDTO actualizar(Integer id, CuentaContableRequestDTO request);

    void eliminar(Integer id);
}