package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.CategoriaRequestDTO;
import com.everywhere.backend.model.dto.CategoriaResponseDTO;
import com.everywhere.backend.security.RequirePermission;
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
    @RequirePermission(module = "CATEGORIA", permission = "READ")
	public List<CategoriaResponseDTO> getAll() {
		return categoriaService.findAll();
	}

	@GetMapping("/{id}")
    @RequirePermission(module = "CATEGORIA", permission = "READ")
	public ResponseEntity<CategoriaResponseDTO> getById(@PathVariable int id) {
		return ResponseEntity.ok(categoriaService.findById(id));
	}

	@PostMapping
    @RequirePermission(module = "CATEGORIA", permission = "CREATE")
	public ResponseEntity<CategoriaResponseDTO> create(@RequestBody CategoriaRequestDTO dto) {
		return ResponseEntity.ok(categoriaService.create(dto));
	}

	@PutMapping("/{id}")
    @RequirePermission(module = "CATEGORIA", permission = "UPDATE")
	public ResponseEntity<CategoriaResponseDTO> update(@PathVariable int id, @RequestBody CategoriaRequestDTO dto) {
		return ResponseEntity.ok(categoriaService.update(id, dto));
	}

	@DeleteMapping("/{id}")
    @RequirePermission(module = "CATEGORIA", permission = "DELETE")
	public ResponseEntity<Void> delete(@PathVariable int id) {
		categoriaService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
