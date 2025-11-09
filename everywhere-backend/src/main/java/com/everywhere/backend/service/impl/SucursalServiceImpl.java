package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.BadRequestException;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.SucursalMapper;
import com.everywhere.backend.model.dto.SucursalRequestDTO;
import com.everywhere.backend.model.dto.SucursalResponseDTO;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.SucursalRepository;
import com.everywhere.backend.service.SucursalService;
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
@CacheConfig(cacheNames = "sucursales")
@Transactional(readOnly = true)
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final SucursalMapper sucursalMapper;

    @Override
    @Cacheable
    public List<SucursalResponseDTO> findAll() {
        return mapToResponseList(sucursalRepository.findAll());
    }

    @Override
    @Cacheable(value = "sucursalById", key = "#id")
    public SucursalResponseDTO findById(Integer id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));
        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Override
    @Cacheable(value = "sucursalesByDescripcion", key = "#descripcion")
    public List<SucursalResponseDTO> findByDescripcion(String descripcion) {
        return mapToResponseList(sucursalRepository.findByDescripcionContainingIgnoreCase(descripcion));
    }

    @Override
    @Cacheable(value = "sucursalByDescripcionExacta", key = "#descripcion")
    public SucursalResponseDTO findByDescripcionExacta(String descripcion) {
        Sucursal sucursal = sucursalRepository.findByDescripcionIgnoreCase(descripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con descripción: " + descripcion));
        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Override
    @Cacheable(value = "sucursalesByEstado", key = "#estado")
    public List<SucursalResponseDTO> findByEstado(Boolean estado) {
        return mapToResponseList(sucursalRepository.findByEstado(estado));
    }

    @Override
    @Cacheable(value = "sucursalesByEstadoAndDescripcion", key = "{#estado, #descripcion}")
    public List<SucursalResponseDTO> findByEstadoAndDescripcion(Boolean estado, String descripcion) {
        return mapToResponseList(sucursalRepository.findByEstadoAndDescripcionContainingIgnoreCase(estado, descripcion));
    }

    @Override
    @Cacheable(value = "sucursalesByDireccion", key = "#direccion")
    public List<SucursalResponseDTO> findByDireccion(String direccion) {
        return mapToResponseList(sucursalRepository.findByDireccionContainingIgnoreCase(direccion));
    }

    @Override
    @Cacheable(value = "sucursalByEmail", key = "#email")
    public SucursalResponseDTO findByEmail(String email) {
        Sucursal sucursal = sucursalRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con email: " + email));
        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "sucursales", "sucursalesByDescripcion", "sucursalesByEstado", "sucursalesByEstadoAndDescripcion", "sucursalesByDireccion",
            "cotizaciones", "cotizacionesSinLiquidacion"
        },
        allEntries = true)
    public SucursalResponseDTO save(SucursalRequestDTO sucursalRequestDTO) {
        if (sucursalRequestDTO.getEmail() != null &&
                !sucursalRequestDTO.getEmail().trim().isEmpty() &&
                sucursalRepository.existsByEmail(sucursalRequestDTO.getEmail())) {
            throw new DataIntegrityViolationException("Ya existe una sucursal con el email: " + sucursalRequestDTO.getEmail());
        }

        Sucursal sucursal = sucursalMapper.toEntity(sucursalRequestDTO);
        if (sucursal.getEstado() == null) sucursal.setEstado(true);

        return sucursalMapper.toResponseDTO(sucursalRepository.save(sucursal));
    }

    @Override
    @Transactional
    @CachePut(value = "sucursalById", key = "#id")
    @CacheEvict(
        value = {
            "sucursales", "sucursalesByDescripcion", "sucursalByDescripcionExacta", "sucursalesByEstado", 
            "sucursalesByEstadoAndDescripcion", "sucursalesByDireccion", "sucursalByEmail",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion"
        },
        allEntries = true)
    public SucursalResponseDTO update(Integer id, SucursalRequestDTO sucursalRequestDTO) {
        if (!sucursalRepository.existsById(id))
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + id);

        Sucursal existing = sucursalRepository.findById(id).get();

        if (sucursalRequestDTO.getEmail() != null &&
                !sucursalRequestDTO.getEmail().trim().isEmpty() &&
                !sucursalRequestDTO.getEmail().equals(existing.getEmail()) &&
                sucursalRepository.existsByEmail(sucursalRequestDTO.getEmail())) {
            throw new BadRequestException("Ya existe una sucursal con el email: " + sucursalRequestDTO.getEmail());
        }

        sucursalMapper.updateEntityFromDTO(sucursalRequestDTO, existing);
        return sucursalMapper.toResponseDTO(sucursalRepository.save(existing));
    }

    @Override
    @Transactional
    @CacheEvict(
        value = {
            "sucursales", "sucursalById", "sucursalesByDescripcion", "sucursalByDescripcionExacta", "sucursalesByEstado", 
            "sucursalesByEstadoAndDescripcion", "sucursalesByDireccion", "sucursalByEmail",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion"
        },
        allEntries = true)
    public void deleteById(Integer id) {
        if (!sucursalRepository.existsById(id))
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + id);
        sucursalRepository.deleteById(id);
    }

    @Override
    @Transactional
    @CachePut(value = "sucursalById", key = "#id")
    @CacheEvict(
        value = {
            "sucursales", "sucursalesByDescripcion", "sucursalByDescripcionExacta", "sucursalesByEstado", 
            "sucursalesByEstadoAndDescripcion", "sucursalesByDireccion", "sucursalByEmail",
            "cotizaciones", "cotizacionById", "cotizacionConDetalles", "cotizacionesSinLiquidacion"
        },
        allEntries = true)
    public SucursalResponseDTO cambiarEstado(Integer id, Boolean estado) {
        if (!sucursalRepository.existsById(id))
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + id);

        Sucursal sucursal = sucursalRepository.findById(id).get();
        sucursal.setEstado(estado);
        return sucursalMapper.toResponseDTO(sucursalRepository.save(sucursal));
    }

    private List<SucursalResponseDTO> mapToResponseList(List<Sucursal> sucursales) {
        return sucursales.stream().map(sucursalMapper::toResponseDTO).toList();
    }
}