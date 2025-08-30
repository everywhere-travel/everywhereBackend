package com.everywhere.backend.model.dto;

import com.everywhere.backend.model.entity.Counter;
import com.everywhere.backend.model.entity.EstadoCotizacion;
import com.everywhere.backend.model.entity.FormaPago;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CotizacionResponseDto {

    private int id;
    private String codigoCotizacion;
    private int cantAdultos;
    private int cantNinos;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private LocalDateTime actualizado;
    private String origenDestino;
    private LocalDate fechaSalida;
    private LocalDate fechaRegreso;
    private String moneda;
    private String observacion;

    // Relaciones
    private Counter counter;
    private FormaPago formaPago;
    private EstadoCotizacion estadoCotizacion;
}
