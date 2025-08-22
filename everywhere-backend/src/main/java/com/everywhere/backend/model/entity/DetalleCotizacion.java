package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "detalle_cotizacion")
public class DetalleCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dcot_id_int")
    private Long id;

    @Column(name = "dcot_cant_int")
    private Integer cantidad;

    @Column(name = "dcot_und_int")
    private Integer unidad;

    @Column(name = "dcot_desc_vac", length = 250)
    private String descripcion;

    @Column(name = "dcot_prec_hist_dc", precision = 15, scale = 2)
    private BigDecimal precioHistorico;

    @Column(name = "dcot_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @Column(name = "dcot_upd_tmp")
    private LocalDateTime actualizado;

    @Column(name = "cot_id_int", nullable = false)
    private Long cotizacionId;

    @Column(name = "prod_id_int", nullable = false)
    private Long productoId;

    @Column(name = "prov_id_int", nullable = false)
    private Long proveedorId;
}
