package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DocumentoRequestDTO;
import com.everywhere.backend.model.dto.DocumentoResponseDTO;

import java.util.List;

public interface DocumentoService {
    List<DocumentoResponseDTO> findAll();
    DocumentoResponseDTO findById(int id);
    DocumentoResponseDTO create(DocumentoRequestDTO dto);
    DocumentoResponseDTO update(int id, DocumentoRequestDTO dto);
    void delete(int id);

}
