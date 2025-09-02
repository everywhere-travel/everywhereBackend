package com.everywhere.backend.api;

import com.everywhere.backend.mapper.OperadorMapper;
import com.everywhere.backend.model.dto.OperadorRequestDto;
import com.everywhere.backend.model.dto.OperadorResponseDTO;
import com.everywhere.backend.model.entity.Operador;
import com.everywhere.backend.repository.OperadorRepository;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.OperadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/operadores")
public class OperadorController {

    private OperadorService operadorService;

    public OperadorController(OperadorService operadorService) {
        this.operadorService = operadorService;
    }

    @GetMapping
    @RequirePermission(module = "CONTABILIDAD", permission = "READ")
    public ResponseEntity<List<OperadorResponseDTO>> findAll() {
        List<OperadorResponseDTO> response = operadorService.findAll()
                .stream()
                .map(OperadorMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "CONTABILIDAD", permission = "READ")
    public ResponseEntity<OperadorResponseDTO> getById(@PathVariable Integer id) {
        return operadorService.findById(id)
                .map(OperadorMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequirePermission(module = "CONTABILIDAD", permission = "CREATE")
    public ResponseEntity<OperadorResponseDTO> create(@RequestBody OperadorRequestDto dto) {
        Operador operador = OperadorMapper.toEntity(dto);
        Operador nuevoOperador = operadorService.save(operador);
        return new ResponseEntity<>(OperadorMapper.toResponse(nuevoOperador), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @RequirePermission(module = "CONTABILIDAD", permission = "UPDATE")
    public ResponseEntity<OperadorResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody OperadorRequestDto dto) {

        return operadorService.findById(id)
                .map(existing -> {
                    existing.setNombre(dto.getNombre());

                    Operador updated = operadorService.update(existing);

                    return ResponseEntity.ok(OperadorMapper.toResponse(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    @RequirePermission(module = "CONTABILIDAD", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (operadorService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        operadorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
