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
    private Long id;

    @Column(name = "liq_num_vac", length = 50)
    private String numero;

    @Column(name = "liq_fec_comp_tmp")
    private LocalDate fechaCompra;

    @Column(name = "liq_fec_vac_tmp")
    private LocalDate fechaVencimiento;

    @Column(name = "liq_dest_vac", length = 255)
    private String destino;

    @Column(name = "liq_nro_pasj_int")
    private Integer numeroPasajeros;

    @Column(name = "liq_obsv_vac", length = 500)
    private String observacion;

    @Column(name = "liq_fec_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @Column(name = "liq_fec_upd_tmp")
    private LocalDateTime actualizado;

    @ManyToOne
    @JoinColumn(name = "cot_id_int", nullable = false)
    private Cotizacion cotizacion;

    @ManyToOne
    @JoinColumn(name = "prod_id_int", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "form_id_int", nullable = false)
    private FormaPago formaPago;

}
