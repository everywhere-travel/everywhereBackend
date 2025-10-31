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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final SucursalMapper sucursalMapper;

    @Override
    public List<SucursalResponseDTO> findAll() {
        return sucursalRepository.findAll().stream().map(sucursalMapper::toResponseDTO).toList();
    }

    @Override
    public SucursalResponseDTO findById(Integer id) {
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));
        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Override
    public List<SucursalResponseDTO> findByDescripcion(String descripcion) {
        List<Sucursal> sucursales = sucursalRepository.findByDescripcionContainingIgnoreCase(descripcion);
        if (sucursales.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron sucursales con la descripcion: " + descripcion);
        }
        return sucursales.stream().map(sucursalMapper::toResponseDTO).toList();
    }

    @Override
    public SucursalResponseDTO findByDescripcionExacta(String descripcion) {
        Sucursal sucursal = sucursalRepository.findByDescripcionIgnoreCase(descripcion)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con descripción: " + descripcion));
        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Override
    public List<SucursalResponseDTO> findByEstado(Boolean estado) {
        List<Sucursal> sucursales = sucursalRepository.findByEstado(estado);
        if (sucursales.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron sucursales con el estado: " + estado);
        }
        return sucursales.stream().map(sucursalMapper::toResponseDTO).toList();
    }


    @Override
    public List<SucursalResponseDTO> findByEstadoAndDescripcion(Boolean estado, String descripcion) {
        List<Sucursal> sucursales = sucursalRepository.findByEstadoAndDescripcionContainingIgnoreCase(estado, descripcion);
        if (sucursales.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se encontraron sucursales con el estado: " + estado + " y descripción que contenga: " + descripcion
            );
        }
        return sucursales.stream()
                .map(sucursalMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<SucursalResponseDTO> findByDireccion(String direccion) {
        List<Sucursal> sucursales = sucursalRepository.findByDireccionContainingIgnoreCase(direccion);
        if (sucursales.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron sucursales con la dirección: " + direccion);
        }
        return sucursales.stream()
                .map(sucursalMapper::toResponseDTO)
                .toList();
    }


    @Override
    public SucursalResponseDTO findByEmail(String email) {
        Sucursal sucursal = sucursalRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con email: " + email));
        return sucursalMapper.toResponseDTO(sucursal);
    }

    @Override
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
    public SucursalResponseDTO update(Integer id, SucursalRequestDTO sucursalRequestDTO) {
        Sucursal existing = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + id));

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
        return sucursalMapper.toResponseDTO(sucursalRepository.save(sucursal));
    }
}
