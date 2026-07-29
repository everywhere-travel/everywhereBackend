package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordChangeRequestDTO {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String email;

    @NotBlank(message = "El código es obligatorio")
    private String code;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String newPassword;
}
