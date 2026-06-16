package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.PersonaRequestDTO;
import com.everywhere.backend.model.dto.PersonaResponseDTO;
import com.everywhere.backend.model.dto.PersonaDisplayDto;
import com.everywhere.backend.model.dto.PersonaTablaDTO;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PersonaService {
    List<PersonaResponseDTO> findAll();
    PersonaResponseDTO findById(Integer id);
    List<PersonaResponseDTO> findByEmail(String email);
    List<PersonaResponseDTO> findByTelefono(String telefono);
    Page<PersonaTablaDTO> findPersonasPage(String searchTerm, String typeFilter, Pageable pageable);
    PersonaResponseDTO save(PersonaRequestDTO personaRequestDTO);
    PersonaResponseDTO patch(Integer id, PersonaRequestDTO personaRequestDTO);
    void deleteById(Integer id);
    PersonaDisplayDto findPersonaNaturalOrJuridicaById(Integer id);
    java.util.Map<String, Long> getPersonaStats();
}
