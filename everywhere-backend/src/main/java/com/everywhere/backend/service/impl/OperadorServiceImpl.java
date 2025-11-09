package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.OperadorMapper;
import com.everywhere.backend.model.dto.OperadorRequestDTO;
import com.everywhere.backend.model.dto.OperadorResponseDTO;
import com.everywhere.backend.model.entity.Operador;
import com.everywhere.backend.repository.OperadorRepository;
import com.everywhere.backend.service.OperadorService;
import lombok.RequiredArgsConstructor;

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
@CacheConfig(cacheNames = "operadores")
@Transactional(readOnly = true)
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository operadorRepository;
    private final OperadorMapper operadorMapper;

    @Override
    @Cacheable
    public List<OperadorResponseDTO> findAll() {
        return mapToResponseList(operadorRepository.findAll());
    }

    @Override
    @Cacheable(value = "operadorById", key = "#id")
    public OperadorResponseDTO findById(int id) {
        Operador operador = operadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operador no encontrado con ID: " + id));
        return operadorMapper.toResponseDTO(operador);
    }

    @Override
    @Cacheable(value = "operadorByNombre", key = "#nombre")
    public OperadorResponseDTO findByNombre(String nombre) {
        Operador operador = operadorRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Operador no encontrado con nombre: " + nombre));

        return operadorMapper.toResponseDTO(operador);
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "operadores", "operadorByNombre",
            "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId",
            "liquidacionConDetalles"
        },
        allEntries = true)
    public OperadorResponseDTO save(OperadorRequestDTO operadorRequestDTO) {
        if (operadorRepository.existsByNombreIgnoreCase(operadorRequestDTO.getNombre()))
            throw new DataIntegrityViolationException("Ya existe un operador con el nombre: " + operadorRequestDTO.getNombre());
        Operador operador = operadorMapper.toEntity(operadorRequestDTO);
        return operadorMapper.toResponseDTO(operadorRepository.save(operador));
    }

    @Override
    @Transactional
    @CachePut(value = "operadorById", key = "#id")
    @CacheEvict(
        value = {
            "operadores", "operadorByNombre",
            "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId",
            "liquidacionConDetalles"
        },
        allEntries = true)
    public OperadorResponseDTO update(int id, OperadorRequestDTO operadorRequestDTO) {
        if (!operadorRepository.existsById(id))
            throw new ResourceNotFoundException("Operador con id " + id + " no encontrado");

        Operador operador = operadorRepository.findById(id).get();

        if (operadorRequestDTO.getNombre() != null && 
            !operadorRequestDTO.getNombre().equalsIgnoreCase(operador.getNombre()) &&
            operadorRepository.existsByNombreIgnoreCase(operadorRequestDTO.getNombre())) {
            throw new DataIntegrityViolationException("Ya existe otro operador con el nombre: " + operadorRequestDTO.getNombre());
        }
        
        operadorMapper.updateEntityFromDTO(operadorRequestDTO, operador);
        return operadorMapper.toResponseDTO(operadorRepository.save(operador));
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "operadores", "operadorById", "operadorByNombre",
            "detallesLiquidacion", "detalleLiquidacionById", "detallesPorLiquidacionId", "detallesSinLiquidacionPorId",
            "liquidacionConDetalles"
        },
        allEntries = true)
    public void deleteById(int id) {
        if (!operadorRepository.existsById(id))
            throw new ResourceNotFoundException("Operador no encontrado con ID: " + id);
        operadorRepository.deleteById(id);
    }

    private List<OperadorResponseDTO> mapToResponseList(List<Operador> operadores) {
        return operadores.stream().map(operadorMapper::toResponseDTO).toList();
    }
}
