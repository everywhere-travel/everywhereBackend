package com.everywhere.backend.mapper;

import com.everywhere.backend.model.entity.Categoria;
import com.everywhere.backend.model.dto.CategoriaRequestDTO;
import com.everywhere.backend.model.dto.CategoriaResponseDTO;

public class CategoriaMapper {

	public static Categoria toEntity(CategoriaRequestDTO dto) {
		Categoria categoria = new Categoria();
		categoria.setNombre(dto.getNombre());
		return categoria;
	}

	public static CategoriaResponseDTO toResponseDto(Categoria categoria) {
		CategoriaResponseDTO dto = new CategoriaResponseDTO();
		dto.setId(categoria.getId());
		dto.setNombre(categoria.getNombre());
		dto.setCreado(categoria.getCreado());
		dto.setActualizado(categoria.getActualizado());
		return dto;
	}
}
