package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class TelefonoPersonaRequestDTO {
    private String numero;
    private String codigoPais;
    private String tipo;
    private String descripcion;
}
