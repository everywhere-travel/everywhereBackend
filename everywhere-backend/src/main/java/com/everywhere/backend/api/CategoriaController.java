package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.CategoriaResponseDto;
import com.everywhere.backend.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService categoriaService;

	@GetMapping
	public List<CategoriaResponseDto> getAll() {
		return categoriaService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoriaResponseDto> getById(@PathVariable int id) {
		return ResponseEntity.ok(categoriaService.findById(id));
	}

	@PostMapping
	public ResponseEntity<CategoriaResponseDto> create(@RequestBody CategoriaRequestDto dto) {
		return ResponseEntity.ok(categoriaService.create(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoriaResponseDto> update(@PathVariable int id, @RequestBody CategoriaRequestDto dto) {
		return ResponseEntity.ok(categoriaService.update(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable int id) {
		categoriaService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
