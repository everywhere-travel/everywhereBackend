package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.CategoriaResponseDto;
import com.everywhere.backend.model.entity.Categoria; 
import com.everywhere.backend.repository.CategoriaRepository;
import com.everywhere.backend.exceptions.ResourceNotFoundException;
import com.everywhere.backend.mapper.CategoriaMapper;
import com.everywhere.backend.service.CategoriaService;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List; 

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@CacheConfig(cacheNames = "categorias")
public class CategoriaServiceImpl implements CategoriaService {

	private final CategoriaRepository categoriaRepository;
	private final CategoriaMapper categoriaMapper;

	@Override
	@Cacheable
	public List<CategoriaResponseDto> findAll() {
		return mapToResponseList(categoriaRepository.findAll());
	}

	@Override
	@Cacheable(value = "categoriaById", key = "#id")
	public CategoriaResponseDto findById(int id) {
		Categoria categoria = categoriaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
		return categoriaMapper.toResponseDto(categoria);
	}

	@Override
	@Transactional
	@CacheEvict(
		value = {
			"categorias",
			"cotizacionConDetalles", "detallesCotizacion", "detalleCotizacionById", "detallesPorCotizacionId"
    }, allEntries = true)
	public CategoriaResponseDto create(CategoriaRequestDto categoriaRequestDto) {
		if(categoriaRepository.existsByNombreIgnoreCase(categoriaRequestDto.getNombre()))
			throw new DataIntegrityViolationException("Ya existe una categoría con el nombre: " + categoriaRequestDto.getNombre());
		Categoria categoria = categoriaMapper.toEntity(categoriaRequestDto); 
		return categoriaMapper.toResponseDto(categoriaRepository.save(categoria));
	}

	@Override
	@Transactional
	@CachePut(value = "categoriaById", key = "#id")
	@CacheEvict(
		value = {
			"categorias",
			"cotizacionConDetalles", "detallesCotizacion", "detalleCotizacionById", "detallesPorCotizacionId"
    	}, allEntries = true)
	public CategoriaResponseDto patch(int id, CategoriaRequestDto categoriaRequestDto) {
		if (!categoriaRepository.existsById(id))
			throw new ResourceNotFoundException("Categoria no encontrada con ID: " + id);

		if (categoriaRequestDto.getNombre() != null && 
			categoriaRepository.existsByNombreIgnoreCase(categoriaRequestDto.getNombre())) {
			Categoria categoria = categoriaRepository.findById(id).get();
			if (!categoriaRequestDto.getNombre().equalsIgnoreCase(categoria.getNombre()))
				throw new DataIntegrityViolationException("Ya existe una categoría con el nombre: " + categoriaRequestDto.getNombre());
		}

		categoriaMapper.updateEntityFromDTO(categoriaRequestDto, categoriaRepository.findById(id).get());
		return categoriaMapper.toResponseDto(categoriaRepository.save(categoriaRepository.findById(id).get()));
	}

	@Override
	@Transactional
	@CacheEvict(
		value = {
			"categorias", "categoriaById",
			"cotizacionConDetalles", "detallesCotizacion", "detalleCotizacionById", "detallesPorCotizacionId"
    	}, allEntries = true)
	public void delete(int id) {
		if (!categoriaRepository.existsById(id))
			throw new ResourceNotFoundException("Categoria no encontrada con ID: " + id);
		categoriaRepository.deleteById(id);
	}

	private List<CategoriaResponseDto> mapToResponseList(List<Categoria> categorias) {
        return categorias.stream().map(categoriaMapper::toResponseDto).toList();
    }
}