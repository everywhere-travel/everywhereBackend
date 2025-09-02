package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class PersonaRequestDTO {

    @Email(message = "El email debe tener un formato válido")
    private String email;
    private String telefono;
    private String direccion;
    private String observacion;
}
