package com.everywhere.backend.api;

import com.everywhere.backend.mapper.ProveedorMapper;
import com.everywhere.backend.model.dto.ProveedorRequestDTO;
import com.everywhere.backend.model.dto.ProveedorResponseDTO;
import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.ProveedorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService){
        this.proveedorService = proveedorService;
    }

    @GetMapping
    @RequirePermission(module = "PROVEEDORES", permission = "READ")
    public ResponseEntity<List<ProveedorResponseDTO>> findAll(){
        List<ProveedorResponseDTO> response = proveedorService.findAll()
                .stream()
                .map(ProveedorMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "READ")
    public ResponseEntity<ProveedorResponseDTO> getById(@PathVariable Integer id) {
        return proveedorService.findById(id)
                .map(ProveedorMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequirePermission(module = "PROVEEDORES", permission = "CREATE")
    public ResponseEntity<ProveedorResponseDTO> create(@RequestBody ProveedorRequestDTO dto) {
        Proveedor proveedor = ProveedorMapper.toEntity(dto);
        Proveedor nuevoProveedor = proveedorService.save(proveedor);
        return new ResponseEntity<>(ProveedorMapper.toResponse(nuevoProveedor), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "UPDATE")
    public ResponseEntity<ProveedorResponseDTO> update(@PathVariable Integer id,
                                                       @RequestBody ProveedorRequestDTO dto) {
        return proveedorService.findById(id)
                .map(existing -> {
                    existing.setNombre(dto.getNombre());
                    Proveedor updated = proveedorService.update(existing);
                    return ResponseEntity.ok(ProveedorMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "PROVEEDORES", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (proveedorService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        proveedorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
