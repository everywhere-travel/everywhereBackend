package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "liquidacion")
public class Liquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "liq_id_int")
    private Integer id;

    @Column(name = "liq_num_vac")
    private String numero;

    @Column(name = "liq_fec_comp_tmp")
    private LocalDate fechaCompra;

    @Column(name = "liq_fec_vac_tmp")
    private LocalDate fechaVencimiento;

    @Column(name = "liq_dest_vac")
    private String destino;

    @Column(name = "liq_nro_pasj_int")
    private Integer numeroPasajeros;

    @Column(name = "liq_obsv_vac")
    private String observacion;

    @Column(name = "liq_fec_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @Column(name = "liq_fec_upd_tmp")
    private LocalDateTime actualizado;

    @ManyToOne
    @JoinColumn(name = "cot_id_int")
    private Cotizacion cotizacion;

    @ManyToOne
    @JoinColumn(name = "prod_id_int", nullable = true)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "form_id_int", nullable = true)
    private FormaPago formaPago;

    @ManyToOne
    @JoinColumn(name = "carp_id_padr_int", nullable = true)
    private Carpeta carpeta;
}
