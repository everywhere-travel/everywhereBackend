package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.CategoriaResponseDto;
import com.everywhere.backend.model.entity.Categoria;
import com.everywhere.backend.repository.CategoriaRepository;
import com.everywhere.backend.mapper.CategoriaMapper;
import com.everywhere.backend.service.CategoriaService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List; 

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

	private final CategoriaRepository categoriaRepository;
	private final CategoriaMapper categoriaMapper;

	@Override
	public List<CategoriaResponseDto> findAll() {
		return categoriaRepository.findAll()
				.stream().map(categoriaMapper::toResponseDto).toList();
	}

	@Override
	public CategoriaResponseDto findById(int id) {
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
		return categoriaMapper.toResponseDto(categoria);
	}

	@Override
	public CategoriaResponseDto create(CategoriaRequestDto categoriaRequestDto) {
		if(categoriaRepository.existsByNombreIgnoreCase(categoriaRequestDto.getNombre()))
			throw new RuntimeException("Ya existe una categoría con el nombre: " + categoriaRequestDto.getNombre());
		Categoria categoria = categoriaMapper.toEntity(categoriaRequestDto); 
		return categoriaMapper.toResponseDto(categoriaRepository.save(categoria));
	}

	@Override
	public CategoriaResponseDto patch(int id, CategoriaRequestDto categoriaRequestDto) {
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoria no encontrada con ID: " + id));

		if (categoriaRequestDto.getNombre() != null && 
			!categoriaRequestDto.getNombre().equalsIgnoreCase(categoria.getNombre()) &&
			categoriaRepository.existsByNombreIgnoreCase(categoriaRequestDto.getNombre()))
			throw new RuntimeException("Ya existe una categoría con el nombre: " + categoriaRequestDto.getNombre());

		categoriaMapper.updateEntityFromDTO(categoriaRequestDto, categoria);
		return categoriaMapper.toResponseDto(categoriaRepository.save(categoria));
	}

	@Override
	public void delete(int id) {
		if (!categoriaRepository.existsById(id))
			throw new RuntimeException("Categoria no encontrada con ID: " + id);
		categoriaRepository.deleteById(id);
	}
}