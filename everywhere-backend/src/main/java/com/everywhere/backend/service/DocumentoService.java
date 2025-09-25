package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.DocumentoRequestDto;
import com.everywhere.backend.model.dto.DocumentoResponseDto;
import com.everywhere.backend.model.entity.Documento;

import java.util.List;

public interface DocumentoService {
    List<DocumentoResponseDto> findAll();
    DocumentoResponseDto findById(int id);
    DocumentoResponseDto create(DocumentoRequestDto dto);
    DocumentoResponseDto update(int id, DocumentoRequestDto dto);
    void delete(int id);

}
