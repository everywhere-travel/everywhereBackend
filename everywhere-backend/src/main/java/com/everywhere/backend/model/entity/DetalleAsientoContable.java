package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "accounting_entry_detail")
public class DetalleAsientoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "det_ent_id_int")
    private int id;

    @ManyToOne
    @JoinColumn(name = "ent_id_int", nullable = false)
    private AsientoContable asiento;

    @ManyToOne
    @JoinColumn(name = "acc_id_int", nullable = false)
    private CuentaContable cuenta;

    @Column(name = "det_ent_debe_dec", precision = 12, scale = 2)
    private BigDecimal debe;

    @Column(name = "det_ent_haber_dec", precision = 12, scale = 2)
    private BigDecimal haber;

    @CreationTimestamp
    @Column(name = "det_ent_cre_tmp")
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "det_ent_upd_tmp")
    private LocalDateTime actualizado;
}