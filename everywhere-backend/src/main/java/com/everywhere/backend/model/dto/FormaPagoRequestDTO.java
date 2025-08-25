package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Data
public class FormaPagoRequestDTO {

    @Min(value = 1, message = "El código debe ser mayor a 0")
    private Integer codigo;

    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    private String descripcion;
}
