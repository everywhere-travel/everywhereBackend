package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DocumentoRequestDto;
import com.everywhere.backend.model.dto.DropdownResponseDTO;
import com.everywhere.backend.model.dto.DocumentoResponseDto;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.DocumentoService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    @GetMapping
    @RequirePermission(module = "DOCUMENTO_CLIENTE", permission = "READ")
    public ResponseEntity<List<DocumentoResponseDto>> getAll() {
        return ResponseEntity.ok(documentoService.findAll());
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "DOCUMENTO_CLIENTE", permission = "READ")
    public ResponseEntity<DocumentoResponseDto> getById(@PathVariable int id) {
        return ResponseEntity.ok(documentoService.findById(id));
    }

    @PostMapping
    @RequirePermission(module = "DOCUMENTO_CLIENTE", permission = "CREATE")
    public ResponseEntity<DocumentoResponseDto> create(@RequestBody DocumentoRequestDto documentoRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoService.create(documentoRequestDto));
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "DOCUMENTO_CLIENTE", permission = "UPDATE")
    public ResponseEntity<DocumentoResponseDto> update(@PathVariable int id, @RequestBody DocumentoRequestDto documentoRequestDto) {
        return ResponseEntity.ok(documentoService.patch(id, documentoRequestDto));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "DOCUMENTO_CLIENTE", permission = "DELETE")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        documentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<DropdownResponseDTO>> getDropdown() {
        return ResponseEntity.ok(documentoService.getDropdown());
    }
}
