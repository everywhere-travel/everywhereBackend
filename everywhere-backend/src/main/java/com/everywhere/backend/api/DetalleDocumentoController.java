package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.DetalleDocumentoResponseDto;
import com.everywhere.backend.model.dto.DetalleDocumentoRequestDto;
import com.everywhere.backend.service.DetalleDocumentoService;
import com.everywhere.backend.security.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalle-documento")
@RequiredArgsConstructor
public class DetalleDocumentoController {

    private final DetalleDocumentoService detalleDocumentoService;

    @GetMapping
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public List<DetalleDocumentoResponseDto> findAll() {
        return detalleDocumentoService.findAll();
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public DetalleDocumentoResponseDto findById(@PathVariable Integer id) {
        return detalleDocumentoService.findById(id);
    }

    @GetMapping("/documento/{documentoId}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public List<DetalleDocumentoResponseDto> findByDocumentoId(@PathVariable Integer documentoId) {
        return detalleDocumentoService.findByDocumentoId(documentoId);
    }

    @GetMapping("/numero/{numero}")
    @RequirePermission(module = "DOCUMENTOS", permission = "READ")
    public List<DetalleDocumentoResponseDto> findByNumero(@PathVariable String numero) {
        return detalleDocumentoService.findByNumero(numero);
    }

    @PostMapping
    @RequirePermission(module = "DOCUMENTOS", permission = "CREATE")
    public DetalleDocumentoResponseDto save(@RequestBody DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        return detalleDocumentoService.save(detalleDocumentoRequestDto);
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "DOCUMENTOS", permission = "UPDATE")
    public DetalleDocumentoResponseDto update( @PathVariable Integer id, @RequestBody DetalleDocumentoRequestDto detalleDocumentoRequestDto) {
        return detalleDocumentoService.update(id, detalleDocumentoRequestDto);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "VIAJEROS", permission = "DELETE")
    public void delete(@PathVariable Integer id) {
        detalleDocumentoService.delete(id);
    }
}