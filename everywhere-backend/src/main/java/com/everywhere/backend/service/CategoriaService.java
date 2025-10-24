package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.CategoriaResponseDto;
import java.util.List;

public interface CategoriaService {
	List<CategoriaResponseDto> findAll();
	CategoriaResponseDto findById(int id);
	CategoriaResponseDto create(CategoriaRequestDto dto);
	CategoriaResponseDto update(int id, CategoriaRequestDto dto);
	void delete(int id);
}