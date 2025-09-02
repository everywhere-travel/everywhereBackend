package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.SucursalRequestDTO;
import com.everywhere.backend.model.dto.SucursalResponseDTO;
import com.everywhere.backend.model.entity.Sucursal;
import com.everywhere.backend.repository.SucursalRepository;
import com.everywhere.backend.service.SucursalService;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;

    @Override
    public List<SucursalResponseDTO> findAll() {
        return sucursalRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SucursalResponseDTO findById(Integer id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));
        return convertToResponseDTO(sucursal);
    }

    @Override
    public List<SucursalResponseDTO> findByDescripcion(String descripcion) {
        List<Sucursal> sucursales = sucursalRepository.findByDescripcionContainingIgnoreCase(descripcion);
        return sucursales.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SucursalResponseDTO findByDescripcionExacta(String descripcion) {
        Sucursal sucursal = sucursalRepository.findByDescripcionIgnoreCase(descripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con descripción: " + descripcion));
        return convertToResponseDTO(sucursal);
    }

    @Override
    public List<SucursalResponseDTO> findByEstado(Boolean estado) {
        List<Sucursal> sucursales = sucursalRepository.findByEstado(estado);
        return sucursales.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SucursalResponseDTO> findByEstadoAndDescripcion(Boolean estado, String descripcion) {
        List<Sucursal> sucursales = sucursalRepository.findByEstadoAndDescripcionContainingIgnoreCase(estado, descripcion);
        return sucursales.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SucursalResponseDTO> findByDireccion(String direccion) {
        List<Sucursal> sucursales = sucursalRepository.findByDireccionContainingIgnoreCase(direccion);
        return sucursales.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SucursalResponseDTO findByEmail(String email) {
        Sucursal sucursal = sucursalRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con email: " + email));
        return convertToResponseDTO(sucursal);
    }

    @Override
    public SucursalResponseDTO save(SucursalRequestDTO sucursalRequestDTO) {
        // Validar email único si se proporciona
        if (sucursalRequestDTO.getEmail() != null &&
            !sucursalRequestDTO.getEmail().trim().isEmpty() &&
            sucursalRepository.existsByEmail(sucursalRequestDTO.getEmail())) {
            throw new BadRequestException("Ya existe una sucursal con el email: " + sucursalRequestDTO.getEmail());
        }

        Sucursal sucursal = convertRequestToEntity(sucursalRequestDTO);
        // Si no se especifica estado, por defecto será activa
        if (sucursal.getEstado() == null) {
            sucursal.setEstado(true);
        }

        sucursal = sucursalRepository.save(sucursal);
        return convertToResponseDTO(sucursal);
    }

    @Override
    public SucursalResponseDTO update(Integer id, SucursalRequestDTO sucursalRequestDTO) {
        Sucursal existingSucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));

        // Validar email único si se intenta cambiar
        if (sucursalRequestDTO.getEmail() != null &&
            !sucursalRequestDTO.getEmail().trim().isEmpty() &&
            !sucursalRequestDTO.getEmail().equals(existingSucursal.getEmail()) &&
            sucursalRepository.existsByEmail(sucursalRequestDTO.getEmail())) {
            throw new BadRequestException("Ya existe una sucursal con el email: " + sucursalRequestDTO.getEmail());
        }

        // Actualizar solo los campos enviados
        if (sucursalRequestDTO.getDescripcion() != null &&
            !sucursalRequestDTO.getDescripcion().trim().isEmpty()) {
            existingSucursal.setDescripcion(sucursalRequestDTO.getDescripcion());
        }

        if (sucursalRequestDTO.getDireccion() != null) {
            existingSucursal.setDireccion(sucursalRequestDTO.getDireccion());
        }

        if (sucursalRequestDTO.getTelefono() != null) {
            existingSucursal.setTelefono(sucursalRequestDTO.getTelefono());
        }

        if (sucursalRequestDTO.getEmail() != null) {
            existingSucursal.setEmail(sucursalRequestDTO.getEmail().trim().isEmpty() ? null : sucursalRequestDTO.getEmail());
        }

        if (sucursalRequestDTO.getEstado() != null) {
            existingSucursal.setEstado(sucursalRequestDTO.getEstado());
        }

        existingSucursal = sucursalRepository.save(existingSucursal);
        return convertToResponseDTO(existingSucursal);
    }

    @Override
    public void deleteById(Integer id) {
        if (!sucursalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sucursal no encontrada con ID: " + id);
        }
        sucursalRepository.deleteById(id);
    }

    @Override
    public SucursalResponseDTO cambiarEstado(Integer id, Boolean estado) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));

        sucursal.setEstado(estado);
        sucursal = sucursalRepository.save(sucursal);
        return convertToResponseDTO(sucursal);
    }

    private SucursalResponseDTO convertToResponseDTO(Sucursal sucursal) {
        SucursalResponseDTO dto = new SucursalResponseDTO();
        dto.setId(sucursal.getId());
        dto.setDescripcion(sucursal.getDescripcion());
        dto.setDireccion(sucursal.getDireccion());
        dto.setTelefono(sucursal.getTelefono());
        dto.setEmail(sucursal.getEmail());
        dto.setEstado(sucursal.getEstado());
        dto.setFechaCreacion(sucursal.getFechaCreacion());
        dto.setFechaActualizacion(sucursal.getFechaActualizacion());
        return dto;
    }

    private Sucursal convertRequestToEntity(SucursalRequestDTO dto) {
        Sucursal entity = new Sucursal();
        entity.setDescripcion(dto.getDescripcion());
        entity.setDireccion(dto.getDireccion());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        entity.setEstado(dto.getEstado());
        return entity;
    }
}
