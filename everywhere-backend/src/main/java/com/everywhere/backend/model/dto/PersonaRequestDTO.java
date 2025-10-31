package com.everywhere.backend.model.dto;

import lombok.Data;
import jakarta.validation.constraints.Email; 

@Data
public class PersonaRequestDTO {

    private String direccion;
    private String observacion;
}
