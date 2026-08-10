package com.everywhere.backend.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CotizacionConDetallesResponseDTO {

    private int id;
    private String nombreCotizacion;
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

    private String clienteNombre;
    private String clienteIdentificador;


    private Integer grupoSeleccionadoId;

    // Relaciones de la cotización
    private CounterResponseDto counter;
    private FormaPagoResponseDTO formaPago;
    private EstadoCotizacionResponseDTO estadoCotizacion;
    private SucursalResponseDTO sucursal;
    private CarpetaResponseDto carpeta;
    private PersonaResponseDTO personas;

    // Lista de detalles anidados (SIN la cotización repetida)
    private List<DetalleCotizacionSimpleDTO> detalles;
}