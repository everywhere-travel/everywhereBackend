package com.everywhere.backend.api;

import com.everywhere.backend.model.dto.FormaPagoRequestDTO;
import com.everywhere.backend.model.dto.FormaPagoResponseDTO;
import com.everywhere.backend.security.RequirePermission;
import com.everywhere.backend.service.FormaPagoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/formas-pago")
@AllArgsConstructor
public class FormaPagoController {

    @Autowired
    private FormaPagoService formaPagoService;

    @GetMapping
    @RequirePermission(module = "FORMA-PAGO", permission = "READ")
    public ResponseEntity<List<FormaPagoResponseDTO>> getAllFormasPago() {
        List<FormaPagoResponseDTO> formasPago = formaPagoService.findAll();
        return ResponseEntity.ok(formasPago);
    }

    @GetMapping("/{id}")
    @RequirePermission(module = "FORMA-PAGO", permission = "READ")
    public ResponseEntity<FormaPagoResponseDTO> getFormaPagoById(@PathVariable Integer id) {
        FormaPagoResponseDTO formaPago = formaPagoService.findById(id);
        return ResponseEntity.ok(formaPago);
    }

    @GetMapping("/codigo/{codigo}")
    @RequirePermission(module = "FORMA-PAGO", permission = "READ")
    public ResponseEntity<FormaPagoResponseDTO> getFormaPagoByCodigo(@PathVariable Integer codigo) {
        FormaPagoResponseDTO formaPago = formaPagoService.findByCodigo(codigo);
        return ResponseEntity.ok(formaPago);
    }

    @GetMapping("/descripcion")
    @RequirePermission(module = "FORMA-PAGO", permission = "READ")
    public ResponseEntity<List<FormaPagoResponseDTO>> getFormasPagoByDescripcion(@RequestParam String descripcion) {
        List<FormaPagoResponseDTO> formasPago = formaPagoService.findByDescripcion(descripcion);
        return ResponseEntity.ok(formasPago);
    }

    @PostMapping
    @RequirePermission(module = "FORMA-PAGO", permission = "CREATE")
    public ResponseEntity<FormaPagoResponseDTO> createFormaPago(@Valid @RequestBody FormaPagoRequestDTO formaPagoRequestDTO) {
        FormaPagoResponseDTO nuevaFormaPago = formaPagoService.save(formaPagoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFormaPago);
    }

    @PatchMapping("/{id}")
    @RequirePermission(module = "FORMA-PAGO", permission = "UPDATE")
    public ResponseEntity<FormaPagoResponseDTO> updateFormaPago(@PathVariable Integer id, @Valid @RequestBody FormaPagoRequestDTO formaPagoRequestDTO) {
        FormaPagoResponseDTO formaPagoActualizada = formaPagoService.update(id, formaPagoRequestDTO);
        return ResponseEntity.ok(formaPagoActualizada);
    }

    @DeleteMapping("/{id}")
    @RequirePermission(module = "FORMA-PAGO", permission = "DELETE")
    public ResponseEntity<Void> deleteFormaPago(@PathVariable Integer id) {
        formaPagoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
