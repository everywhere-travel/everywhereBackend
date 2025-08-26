package com.everywhere.backend.model.dto;

import lombok.Data;

@Data
public class CounterResponseDto {
    private int id;

    private String nombre;

    private Boolean estado;

    private String codigo;
}
