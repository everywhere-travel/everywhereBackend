package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.RolePermissionRequestDTO;
import com.everywhere.backend.model.dto.RoleRequestDTO;
import com.everywhere.backend.model.dto.RoleResponseDTO;
import com.everywhere.backend.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.everywhere.backend.security.RequirePermission;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @RequirePermission(module = "ROLES", permission = "READ")
    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @RequirePermission(module = "ROLES", permission = "READ")
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @RequirePermission(module = "ROLES", permission = "CREATE")
    @PostMapping
    public ResponseEntity<RoleResponseDTO> create(@Valid @RequestBody RoleRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(request));
    }

    @RequirePermission(module = "ROLES", permission = "UPDATE")
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> update(@PathVariable Integer id,
                                                   @Valid @RequestBody RoleRequestDTO request) {
        return ResponseEntity.ok(roleService.update(id, request));
    }

    @RequirePermission(module = "ROLES", permission = "DELETE")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Gestión de permisos de un rol ---

    @RequirePermission(module = "ROLES", permission = "UPDATE")
    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<RoleResponseDTO> addPermission(@PathVariable Integer roleId,
                                                          @Valid @RequestBody RolePermissionRequestDTO request) {
        return ResponseEntity.ok(roleService.addPermission(roleId, request.getPermissionId()));
    }

    @RequirePermission(module = "ROLES", permission = "UPDATE")
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<RoleResponseDTO> removePermission(@PathVariable Integer roleId,
                                                             @PathVariable Integer permissionId) {
        return ResponseEntity.ok(roleService.removePermission(roleId, permissionId));
    }
}
