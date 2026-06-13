package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.PermissionRequestDTO;
import com.everywhere.backend.model.dto.PermissionResponseDTO;
import com.everywhere.backend.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<PermissionResponseDTO>> findAll() {
        return ResponseEntity.ok(permissionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(permissionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PermissionResponseDTO> create(@Valid @RequestBody PermissionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponseDTO> update(@PathVariable Integer id,
                                                         @Valid @RequestBody PermissionRequestDTO request) {
        return ResponseEntity.ok(permissionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
