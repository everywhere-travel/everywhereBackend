package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "accounting_entry")
public class AsientoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ent_id_int")
    private int id;

    @Column(name = "ent_fecha_dat", nullable = false)
    private LocalDate fecha;

    @Column(name = "ent_glosa_vac", length = 255)
    private String glosa;

    @Column(name = "ent_origen_vac", length = 80)
    private String origen;

    @Column(name = "ent_origen_id_int")
    private Integer origenId;

    @Column(name = "ent_moneda_vac", length = 10)
    private String moneda;

    @Column(name = "ent_total_debe_dec", precision = 12, scale = 2)
    private BigDecimal totalDebe = BigDecimal.ZERO;

    @Column(name = "ent_total_haber_dec", precision = 12, scale = 2)
    private BigDecimal totalHaber = BigDecimal.ZERO;

    @Column(name = "ent_anulado_bool")
    private Boolean anulado = false;

    @Column(name = "ent_gen_auto_bool")
    private Boolean generadoAutomaticamente = false;

    @CreationTimestamp
    @Column(name = "ent_cre_tmp")
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "ent_upd_tmp")
    private LocalDateTime actualizado;

    @OneToMany(
        mappedBy = "asiento",
        cascade = CascadeType.ALL,
        fetch = FetchType.LAZY
    )
    private List<DetalleAsientoContable> detalles;
}