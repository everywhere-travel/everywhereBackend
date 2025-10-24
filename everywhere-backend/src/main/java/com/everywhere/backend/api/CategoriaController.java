package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CategoriaRequestDto;
import com.everywhere.backend.model.dto.CategoriaResponseDto;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.CategoriaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categorias")
public class CategoriaController {

	private final CategoriaService categoriaService;

	@GetMapping
    @RequirePermission(module = "CATEGORIA", permission = "READ")
	public List<CategoriaResponseDto> getAll() {
		return categoriaService.findAll();
	}

	@GetMapping("/{id}")
    @RequirePermission(module = "CATEGORIA", permission = "READ")
	public ResponseEntity<CategoriaResponseDto> getById(@PathVariable int id) {
		return ResponseEntity.ok(categoriaService.findById(id));
	}

	@PostMapping
    @RequirePermission(module = "CATEGORIA", permission = "CREATE")
	public ResponseEntity<CategoriaResponseDto> create(@RequestBody CategoriaRequestDto categoriaRequestDto) {
		return ResponseEntity.ok(categoriaService.create(categoriaRequestDto));
	}

	@PutMapping("/{id}")
    @RequirePermission(module = "CATEGORIA", permission = "UPDATE")
	public ResponseEntity<CategoriaResponseDto> update(@PathVariable int id, @RequestBody CategoriaRequestDto categoriaRequestDto) {
		return ResponseEntity.ok(categoriaService.update(id, categoriaRequestDto));
	}

	@DeleteMapping("/{id}")
    @RequirePermission(module = "CATEGORIA", permission = "DELETE")
	public ResponseEntity<Void> delete(@PathVariable int id) {
		categoriaService.delete(id);
		return ResponseEntity.noContent().build();
	}
}