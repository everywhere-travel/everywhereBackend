package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "accounting_account")
public class CuentaContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acc_id_int")
    private int id;

    @Column(name = "acc_cod_vac", length = 20, nullable = false)
    private String codigo;

    @Column(name = "acc_nom_vac", length = 150, nullable = false)
    private String nombre;

    @Column(name = "acc_tipo_vac", length = 50, nullable = false)
    private String tipo;

    @Column(name = "acc_activo_bool")
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "acc_cre_tmp")
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "acc_upd_tmp")
    private LocalDateTime actualizado;
}