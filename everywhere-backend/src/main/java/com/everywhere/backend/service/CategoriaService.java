package com.everywhere.backend.service;

import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import com.everywhere.backend.model.dto.CategoriaResponseDto;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import java.util.List;
import com.everywhere.backend.model.dto.DropdownResponseDTO;

public interface CategoriaService {
	List<CategoriaResponseDto> findAll();
	CategoriaResponseDto findById(int id);
	CategoriaResponseDto create(CategoriaRequestDto categoriaRequestDto);
	CategoriaResponseDto patch(int id, CategoriaRequestDto categoriaRequestDto);
	void delete(int id);
    List<DropdownResponseDTO> getDropdown();
}
