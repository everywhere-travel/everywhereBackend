package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CategoriaRequestDTO;
import com.everywhere.backend.model.dto.CategoriaResponseDTO;
import java.util.List;

public interface CategoriaService {
	List<CategoriaResponseDTO> findAll();
	CategoriaResponseDTO findById(int id);
	CategoriaResponseDTO create(CategoriaRequestDTO dto);
	CategoriaResponseDTO update(int id, CategoriaRequestDTO dto);
	void delete(int id);
}
