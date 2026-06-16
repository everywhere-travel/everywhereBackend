package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.CuentaContableMapper;
import com.everywhere.backend.model.dto.CuentaContableRequestDTO;
import com.everywhere.backend.model.dto.CuentaContableResponseDTO;
import com.everywhere.backend.model.entity.CuentaContable;
import com.everywhere.backend.repository.CuentaContableRepository;
import com.everywhere.backend.service.CuentaContableService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CuentaContableServiceImpl implements CuentaContableService {

    private final CuentaContableRepository cuentaRepository;
    private final CuentaContableMapper cuentaMapper;

    @Override
    public List<CuentaContableResponseDTO> listar() {
        return cuentaRepository.findAll()
                .stream()
                .map(cuentaMapper::toResponse)
                .toList();
    }

    @Override
    public CuentaContableResponseDTO obtenerPorId(Integer id) {
        CuentaContable cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta contable no encontrada con ID: " + id));

        return cuentaMapper.toResponse(cuenta);
    }

    @Override
    @Transactional
    public CuentaContableResponseDTO crear(CuentaContableRequestDTO request) {
        CuentaContable cuenta = cuentaMapper.toEntity(request);

        if (cuenta.getActivo() == null) {
            cuenta.setActivo(true);
        }

        CuentaContable guardada = cuentaRepository.save(cuenta);

        return cuentaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public CuentaContableResponseDTO actualizar(Integer id, CuentaContableRequestDTO request) {
        CuentaContable cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta contable no encontrada con ID: " + id));

        cuenta.setCodigo(request.getCodigo());
        cuenta.setNombre(request.getNombre());
        cuenta.setTipo(request.getTipo());

        if (request.getActivo() != null) {
            cuenta.setActivo(request.getActivo());
        }

        CuentaContable actualizada = cuentaRepository.save(cuenta);

        return cuentaMapper.toResponse(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        CuentaContable cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta contable no encontrada con ID: " + id));

        cuentaRepository.delete(cuenta);
    }
}