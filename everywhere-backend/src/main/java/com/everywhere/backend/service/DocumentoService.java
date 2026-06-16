package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DocumentoRequestDto;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import com.everywhere.backend.model.dto.DocumentoResponseDto;
import com.everywhere.backend.model.dto.DropdownResponseDTO;

import java.util.List;
import com.everywhere.backend.model.dto.DropdownResponseDTO;

public interface DocumentoService {
    List<DocumentoResponseDto> findAll();
    DocumentoResponseDto findById(int id);
    DocumentoResponseDto create(DocumentoRequestDto documentoRequestDto);
    DocumentoResponseDto patch(int id, DocumentoRequestDto documentoRequestDto);
    void delete(int id);
    List<DropdownResponseDTO> getDropdown();
}
