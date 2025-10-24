package com.everywhere.backend.model.dto;

import com.everywhere.backend.model.entity.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CotizacionResponseDto {

    private int id;
    private String codigoCotizacion;
    private int cantAdultos;
    private int cantNinos;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaVencimiento;
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
    private Sucursal sucursal;
    private Carpeta carpeta;
    private Personas personas;
}
