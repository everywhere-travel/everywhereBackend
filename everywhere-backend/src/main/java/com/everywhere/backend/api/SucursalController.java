package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.SucursalRequestDTO;
import com.everywhere.backend.model.dto.SucursalResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    @RequirePermission(module = "SUCURSALES", permission = "READ")
    public ResponseEntity<List<SucursalResponseDTO>> getAllSucursales() {
        List<SucursalResponseDTO> sucursales = sucursalService.findAll();
        return ResponseEntity.ok(sucursales);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "SUCURSALES", permission = "READ")
    public ResponseEntity<SucursalResponseDTO> getSucursalById(@PathVariable Integer id) {
        SucursalResponseDTO sucursal = sucursalService.findById(id);
        return ResponseEntity.ok(sucursal);
    }

    @GetMapping("/estado/{estado}")
    @RequirePermission(module = "SUCURSALES", permission = "READ")
    public ResponseEntity<List<SucursalResponseDTO>> getSucursalesByEstado(@PathVariable Boolean estado) {
        List<SucursalResponseDTO> sucursales = sucursalService.findByEstado(estado);
        return ResponseEntity.ok(sucursales);
    }

    @PostMapping
    @RequirePermission(module = "SUCURSALES", permission = "CREATE")
    public ResponseEntity<SucursalResponseDTO> createSucursal(@Valid @RequestBody SucursalRequestDTO sucursalRequestDTO) {
        SucursalResponseDTO nuevaSucursal = sucursalService.save(sucursalRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaSucursal);
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "SUCURSALES", permission = "UPDATE")
    public ResponseEntity<SucursalResponseDTO> updateSucursal(
            @PathVariable Integer id,
            @Valid @RequestBody SucursalRequestDTO sucursalRequestDTO) {
        SucursalResponseDTO sucursalActualizada = sucursalService.update(id, sucursalRequestDTO);
        return ResponseEntity.ok(sucursalActualizada);
    }

    @PatchMapping("/{id}/estado")
    @RequirePermission(module = "SUCURSALES", permission = "UPDATE")
    public ResponseEntity<SucursalResponseDTO> cambiarEstadoSucursal(
            @PathVariable Integer id,
            @RequestParam Boolean estado) {
        SucursalResponseDTO sucursalActualizada = sucursalService.cambiarEstado(id, estado);
        return ResponseEntity.ok(sucursalActualizada);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "SUCURSALES", permission = "DELETE")
    public ResponseEntity<Void> deleteSucursal(@PathVariable Integer id) {
        sucursalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
