package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.Valid;

@Data
public class PersonaNaturalRequestDTO {
    private String documento;
    private String nombres;
    private String apellidos;
    private Boolean cliente;
    private String categoria;

    @Valid
    private PersonaRequestDTO persona;
}
