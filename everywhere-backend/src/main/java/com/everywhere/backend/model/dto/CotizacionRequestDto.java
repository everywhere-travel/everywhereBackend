package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CotizacionRequestDto {
    private int cantAdultos;
    private int cantNinos;
    private LocalDate fechaVencimiento;
    private String origenDestino;
    private LocalDate fechaSalida;
    private LocalDate fechaRegreso;
    private String moneda;
    private String observacion;
}
