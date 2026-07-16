package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerificationRequestDTO {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String email;
}
