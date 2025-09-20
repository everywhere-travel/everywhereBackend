package com.everywhere.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "cotizaciones")
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cot_id_int")
    private int id;

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
    private LocalDate fechaRegreso;

    @Column(name = "cot_mon_vac", length = 20)
    private String moneda;

    @Column(name = "cot_obs_vac", length = 300)
    private String observacion;

    @ManyToOne
    @JoinColumn(name = "cou_id_int")
    private Counter counter;

    @ManyToOne
    @JoinColumn(name = "form_id_int")
    private FormaPago formaPago;

    @ManyToOne
    @JoinColumn(name = "est_cot_id_int")
    private EstadoCotizacion estadoCotizacion;

    @ManyToOne
    @JoinColumn(name = "suc_id_int")
    private Sucursal sucursal;

    @ManyToOne
    @JoinColumn (name = "carp_id_padr_int")
    private Carpeta carpeta;

    @ManyToOne
    @JoinColumn(name = "per_id_int")
    private Personas personas;

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<DetalleCotizacion> detalles;


}
