package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ProveedorMapper;
import com.everywhere.backend.model.dto.ProveedorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorResponseDTO;
import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.repository.ProveedorRepository;
import com.everywhere.backend.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    public ProveedorResponseDTO create(ProveedorRequestDTO proveedorRequestDTO) {
        if (proveedorRequestDTO.getRuc() != null && proveedorRepository.existsByRuc(proveedorRequestDTO.getRuc())) {
            throw new DataIntegrityViolationException("Ya existe un proveedor con el RUC: " + proveedorRequestDTO.getRuc());
        }

        Proveedor proveedor = proveedorMapper.toEntity(proveedorRequestDTO);
        return proveedorMapper.toResponseDTO(proveedorRepository.save(proveedor));
    }

    @Override
    public ProveedorResponseDTO update(Integer id, ProveedorRequestDTO proveedorRequestDTO) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));

        if (proveedorRequestDTO.getRuc() != null &&
                !proveedorRequestDTO.getRuc().equals(proveedor.getRuc()) &&
                proveedorRepository.existsByRuc(proveedorRequestDTO.getRuc())) {
            throw new DataIntegrityViolationException("Ya existe un proveedor con el RUC: " + proveedorRequestDTO.getRuc());
        }

        return proveedorMapper.toResponseDTO(proveedorRepository.save(proveedor));
    }

    @Override
    public ProveedorResponseDTO getById(Integer id) {
        var proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con ID: " + id));
        return proveedorMapper.toResponseDTO(proveedor);
    }


    @Override
    public List<ProveedorResponseDTO> getAll() {
        return proveedorRepository.findAll()
                .stream()
                .map(proveedorMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void delete(Integer id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor no encontrado con ID: " + id);
        }
        proveedorRepository.deleteById(id);
    }
}
