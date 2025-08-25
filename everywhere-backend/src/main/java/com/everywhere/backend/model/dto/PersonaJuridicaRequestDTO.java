package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

@Data
public class PersonaJuridicaRequestDTO {

    @Size(max = 20, message = "El RUC no puede superar los 20 caracteres")
    private String ruc;

    private String razonSocial;

    @Valid
    private PersonaRequestDTO persona;
}
