package com.everywhere.backend.mapper;

import com.everywhere.backend.model.entity.Categoria;
import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.CategoriaResponseDto;

public class CategoriaMapper {

	public static Categoria toEntity(CategoriaRequestDto dto) {
		Categoria categoria = new Categoria();
		categoria.setNombre(dto.getNombre());
		return categoria;
	}

	public static CategoriaResponseDto toResponseDto(Categoria categoria) {
		CategoriaResponseDto dto = new CategoriaResponseDto();
		dto.setId(categoria.getId());
		dto.setNombre(categoria.getNombre());
		dto.setCreado(categoria.getCreado());
		dto.setActualizado(categoria.getActualizado());
		return dto;
	}
}
