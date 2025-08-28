package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

@Data
public class PersonaNaturalRequestDTO {
    private String documento;
    private String nombres;
    private String apellidos;

    @Valid
    private PersonaRequestDTO persona;
}
