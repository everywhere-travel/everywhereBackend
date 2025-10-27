package com.everywhere.backend.service.impl;

import com.everywhere.backend.mapper.OperadorMapper;
import com.everywhere.backend.model.dto.OperadorRequestDTO;
import com.everywhere.backend.model.dto.OperadorResponseDTO;
import com.everywhere.backend.model.entity.Operador;
import com.everywhere.backend.repository.OperadorRepository;
import com.everywhere.backend.service.OperadorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository operadorRepository;
    private final OperadorMapper operadorMapper;

    @Override
    public List<OperadorResponseDTO> findAll() {
        return operadorRepository.findAll().stream()
                .map(operadorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OperadorResponseDTO> findById(int id) {
        return operadorRepository.findById(id)
                .map(operadorMapper::toResponseDTO);
    }

    @Override
    public Optional<OperadorResponseDTO> findByNombre(String nombre) {
        return operadorRepository.findByNombre(nombre)
                .map(operadorMapper::toResponseDTO);
    }

    @Override
    public OperadorResponseDTO save(OperadorRequestDTO dto) {
        Operador operador = operadorMapper.toEntity(dto);
        return operadorMapper.toResponseDTO(operadorRepository.save(operador));
    }

    @Override
    public OperadorResponseDTO update(int id, OperadorRequestDTO dto) {
        Operador operador = operadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operador con id " + id + " no encontrado"));
        operadorMapper.updateEntityFromDTO(dto, operador);
        return operadorMapper.toResponseDTO(operadorRepository.save(operador));
    }

    @Override
    public void deleteById(int id) {
        operadorRepository.deleteById(id);
    }
}
