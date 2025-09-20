package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.CategoriaResponseDto;
import com.everywhere.backend.model.entity.Categoria;
import com.everywhere.backend.repository.CategoriaRepository;
import com.everywhere.backend.mapper.CategoriaMapper;
import com.everywhere.backend.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Override
	public List<CategoriaResponseDto> findAll() {
		return categoriaRepository.findAll()
				.stream()
				.map(CategoriaMapper::toResponseDto)
				.collect(Collectors.toList());
	}

	@Override
	public CategoriaResponseDto findById(int id) {
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
		return CategoriaMapper.toResponseDto(categoria);
	}

	@Override
	public CategoriaResponseDto create(CategoriaRequestDto dto) {
		Categoria categoria = CategoriaMapper.toEntity(dto);
		categoria.setCreado(LocalDateTime.now());
		categoria.setActualizado(LocalDateTime.now());
		Categoria saved = categoriaRepository.save(categoria);
		return CategoriaMapper.toResponseDto(saved);
	}

	@Override
	public CategoriaResponseDto update(int id, CategoriaRequestDto dto) {
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
		categoria.setNombre(dto.getNombre());
		categoria.setActualizado(LocalDateTime.now());
		Categoria updated = categoriaRepository.save(categoria);
		return CategoriaMapper.toResponseDto(updated);
	}

	@Override
	public void delete(int id) {
		categoriaRepository.deleteById(id);
	}
}
