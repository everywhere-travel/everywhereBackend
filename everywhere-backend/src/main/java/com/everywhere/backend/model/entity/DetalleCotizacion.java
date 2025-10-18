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
    private int id;

    @Column(name = "dcot_cant_int")
    private Integer cantidad;

    @Column(name = "dcot_und_int")
    private Integer unidad;

    @Column(name = "dcot_desc_vac", length = 250)
    private String descripcion;

    @Column(name = "dcot_prec_hist_dc")
    private BigDecimal precioHistorico;

    @Column(name = "dcot_select_bol")
    private Boolean seleccionado;

    @Column(name = "dcot_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @Column(name = "dcot_upd_tmp", updatable = true)
    private LocalDateTime actualizado;

    @Column(name = "dcot_comision_dc")
    private BigDecimal comision;


    @ManyToOne
    @JoinColumn(name = "cat_id_int")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "cot_id_int", nullable = false)
    private Cotizacion cotizacion;

    @ManyToOne
    @JoinColumn(name = "prod_id_int")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "prov_id_int")
    private Proveedor proveedor;
}
