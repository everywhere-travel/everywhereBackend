package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ProductoRequestDTO;
import com.everywhere.backend.model.dto.ProductoResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // Crear un producto
    @PostMapping
    @RequirePermission(module = "PRODUCTOS", permission = "CREATE")
    public ResponseEntity<ProductoResponseDTO> create(@RequestBody ProductoRequestDTO request) {
        return ResponseEntity.ok(productoService.create(request));
    }

    // Actualizar un producto por id
    @PutMapping("/{id}")
    @RequirePermission(module = "PRODUCTOS", permission = "UPDATE")
    public ResponseEntity<ProductoResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody ProductoRequestDTO request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    // Obtener un producto por id
    @GetMapping("/{id}")
    @RequirePermission(module = "PRODUCTOS", permission = "READ")
    public ResponseEntity<ProductoResponseDTO> getById(@PathVariable Integer id) {
        return productoService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Listar todos los productos
    @GetMapping
    @RequirePermission(module = "PRODUCTOS", permission = "READ")
    public ResponseEntity<List<ProductoResponseDTO>> getAll() {
        return ResponseEntity.ok(productoService.getAll());
    }

    // Eliminar un producto por id
    @DeleteMapping("/{id}")
    @RequirePermission(module = "PRODUCTOS", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener producto por código
    @GetMapping("/codigo/{codigo}")
    @RequirePermission(module = "PRODUCTOS", permission = "READ")
    public ResponseEntity<ProductoResponseDTO> getByCodigo(@PathVariable String codigo) {
        return productoService.getByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}