package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cotizaciones")
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cot_id_int")
    private Long id;

    @Column(name = "cot_num_vac", length = 100)
    private String codigoCotizacion;

    @Column(name = "cot_cant_adlt_int")
    private Integer cantAdultos;

    @Column(name = "cot_cant_dchd_int")
    private Integer cantNinos;

    @Column(name = "cot_fec_emi_tmp")
    private LocalDate fechaEmision;

    @Column(name = "cot_fec_venc_tmp")
    private LocalDate fechaVencimiento;

    @Column(name = "cot_fec_upd_tmp")
    private LocalDateTime actualizado;

    @Column(name = "cot_dest_vac", length = 200)
    private String origenDestino;

    @Column(name = "cot_fec_sal_tmp")
    private LocalDate fechaSalida;

    @Column(name = "cot_fec_reg_tmp")
    private LocalDate fechaRegistro;

    @Column(name = "cot_mon_vac", length = 20)
    private String moneda;

    @Column(name = "cot_obs_vac", length = 300)
    private String observacion;

    @Column(name = "cou_id_int", nullable = false)
    private Long counterId;

    @Column(name = "form_id_int", nullable = false)
    private Long formaPagoId;

    @Column(name = "est_cot_id_int", nullable = false)
    private Long estadoCotizacionId;

    @Column(name = "suc_id_int", nullable = false)
    private Long sucursalId;
}
