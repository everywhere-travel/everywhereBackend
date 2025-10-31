package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.ProveedorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    @RequirePermission(module = "PROVEEDORES", permission = "READ")
    public ResponseEntity<List<ProveedorResponseDTO>> findAll() {
        List<ProveedorResponseDTO> response = proveedorService.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "READ")
    public ResponseEntity<ProveedorResponseDTO> getById(@PathVariable Integer id) {
        return proveedorService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequirePermission(module = "PROVEEDORES", permission = "CREATE")
    public ResponseEntity<ProveedorResponseDTO> create(@RequestBody @Valid ProveedorRequestDTO proveedorRequestDTO) {
        ProveedorResponseDTO created = proveedorService.create(proveedorRequestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "UPDATE")
    public ResponseEntity<ProveedorResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody ProveedorRequestDTO proveedorRequestDTO) {

        ProveedorResponseDTO updated = proveedorService.update(id, proveedorRequestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        proveedorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
