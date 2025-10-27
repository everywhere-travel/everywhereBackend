package com.everywhere.backend.service.impl;

import com.everywhere.backend.exceptions.BadRequestException;
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

import java.time.LocalDateTime;
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
        if (dto.getNumero() == null || dto.getNumero().isBlank())
            throw new BadRequestException("El número de teléfono es obligatorio");
        if (dto.getCodigoPais() == null || dto.getCodigoPais().isBlank())
            throw new BadRequestException("El código de país es obligatorio");
        if (dto.getTipo() == null || dto.getTipo().isBlank())
            throw new BadRequestException("El tipo de teléfono es obligatorio");

        Personas persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + personaId));

        TelefonoPersona telefono = telefonoPersonaMapper.toEntity(dto);
        telefono.setPersona(persona);
        telefono.setCreado(LocalDateTime.now());
        telefono.setActualizado(LocalDateTime.now());

        return telefonoPersonaMapper.toResponseDTO(telefonoPersonaRepository.save(telefono));
    }



    @Override
    public TelefonoPersonaResponseDTO update(Integer personaId, TelefonoPersonaRequestDTO dto, Integer telefonoId) {
        TelefonoPersona telefono = telefonoPersonaRepository.findById(telefonoId)
                .orElseThrow(() -> new ResourceNotFoundException("Teléfono no encontrado con ID: " + telefonoId));

        telefonoPersonaMapper.updateEntityFromDTO(dto, telefono);
        telefono.setActualizado(LocalDateTime.now());

        return telefonoPersonaMapper.toResponseDTO(telefonoPersonaRepository.save(telefono));
    }


    @Override
    public void deleteById(Integer id) {
        if (!telefonoPersonaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Teléfono no encontrado con ID: " + id);
        }
        telefonoPersonaRepository.deleteById(id);
    }
}
