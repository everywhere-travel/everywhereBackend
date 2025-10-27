package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.TelefonoPersonaMapper;
import com.everywhere.backend.model.dto.TelefonoPersonaRequestDTO;
import com.everywhere.backend.model.dto.TelefonoPersonaResponseDTO;
import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.entity.TelefonoPersona;
import com.everywhere.backend.repository.PersonaRepository;
import com.everywhere.backend.repository.TelefonoPersonaRepository;
import com.everywhere.backend.service.TelefonoPersonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelefonoPersonaServiceImpl implements TelefonoPersonaService {

    private final TelefonoPersonaRepository telefonoPersonaRepository;
    private final PersonaRepository personaRepository;
    private final TelefonoPersonaMapper telefonoPersonaMapper;

    @Override
    public List<TelefonoPersonaResponseDTO> findAll() {
        return telefonoPersonaRepository.findAll()
                .stream()
                .map(telefonoPersonaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public Optional<TelefonoPersonaResponseDTO> findById(Integer id) {
        return telefonoPersonaRepository.findById(id)
                .map(telefonoPersonaMapper::toResponseDTO);
    }

    @Override
    public List<TelefonoPersonaResponseDTO> findByPersonaId(Integer personaId) {
        return telefonoPersonaRepository.findByPersonaId(personaId)
                .stream()
                .map(telefonoPersonaMapper::toResponseDTO)
                .toList();
    }

    @Override
    public TelefonoPersonaResponseDTO save(TelefonoPersonaRequestDTO dto, Integer personaId) {
        if (personaId == null) {
            throw new IllegalArgumentException("El personaId no puede ser null");
        }

        Personas persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + personaId));

        if (dto.getNumero() == null || dto.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono es obligatorio");
        }
        if (dto.getCodigoPais() == null || dto.getCodigoPais().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del país es obligatorio");
        }
        if (dto.getTipo() == null || dto.getTipo().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo es obligatorio");
        }

        TelefonoPersona telefono = telefonoPersonaMapper.toEntity(dto);
        telefono.setPersona(persona);

        TelefonoPersona saved = telefonoPersonaRepository.save(telefono);
        return telefonoPersonaMapper.toResponseDTO(saved);
    }


    @Override
    public TelefonoPersonaResponseDTO update(Integer personaId, TelefonoPersonaRequestDTO dto, Integer telefonoId) {
        TelefonoPersona existing = telefonoPersonaRepository.findById(telefonoId)
                .orElseThrow(() -> new ResourceNotFoundException("Teléfono no encontrado con ID: " + telefonoId));

        telefonoPersonaMapper.updateEntityFromDTO(dto, existing);

        if (personaId != null) {
            Personas persona = personaRepository.findById(personaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + personaId));
            existing.setPersona(persona);
        }

        TelefonoPersona updated = telefonoPersonaRepository.save(existing);
        return telefonoPersonaMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteById(Integer id) {
        if (!telefonoPersonaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teléfono no encontrado con ID: " + id);
        }
        telefonoPersonaRepository.deleteById(id);
    }
}
