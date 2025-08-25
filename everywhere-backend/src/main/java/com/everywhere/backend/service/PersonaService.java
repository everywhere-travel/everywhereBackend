package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.PersonaRequestDTO;
import com.everywhere.backend.model.dto.PersonaResponseDTO;

import java.util.List;

public interface PersonaService {
    List<PersonaResponseDTO> findAll();
    PersonaResponseDTO findById(Integer id);
    List<PersonaResponseDTO> findByEmail(String email);
    List<PersonaResponseDTO> findByTelefono(String telefono);
    PersonaResponseDTO save(PersonaRequestDTO personaRequestDTO);
    PersonaResponseDTO update(Integer id, PersonaRequestDTO personaRequestDTO);
    void deleteById(Integer id);
}
