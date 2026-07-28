package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.ProveedorGrupoContactoMapper;
import com.everywhere.backend.model.dto.ProveedorGrupoContactoRequestDTO;
import com.everywhere.backend.model.dto.ProveedorGrupoContactoResponseDTO;
import com.everywhere.backend.model.entity.ProveedorGrupoContacto;
import com.everywhere.backend.repository.ProveedorGrupoContactoRepository;
import com.everywhere.backend.service.ProveedorGrupoContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProveedorGrupoContactoServiceImpl implements ProveedorGrupoContactoService {

    private final ProveedorGrupoContactoRepository repository;
    private final ProveedorGrupoContactoMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorGrupoContactoResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorGrupoContactoResponseDTO findById(Integer id) {
        ProveedorGrupoContacto entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo de contacto no encontrado con ID: " + id));
        return mapper.toResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorGrupoContactoResponseDTO> findByNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ProveedorGrupoContactoResponseDTO save(ProveedorGrupoContactoRequestDTO dto) {
        ProveedorGrupoContacto entity = mapper.toEntity(dto);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Override
    @Transactional
    public ProveedorGrupoContactoResponseDTO update(Integer id, ProveedorGrupoContactoRequestDTO dto) {
        ProveedorGrupoContacto existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo de contacto no encontrado con ID: " + id));

        mapper.updateEntityFromDTO(dto, existing);
        return mapper.toResponseDTO(repository.save(existing));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Grupo de contacto no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}
