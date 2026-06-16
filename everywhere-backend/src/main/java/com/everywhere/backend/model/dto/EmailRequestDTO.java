package com.everywhere.backend.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequestDTO {
    
    @NotBlank(message = "El correo de destino es obligatorio")
    @Email(message = "El correo de destino debe tener un formato válido")
    private String to;
    
    @NotBlank(message = "El asunto es obligatorio")
    private String subject;
    
    @NotBlank(message = "El cuerpo del mensaje es obligatorio")
    private String body;
}
