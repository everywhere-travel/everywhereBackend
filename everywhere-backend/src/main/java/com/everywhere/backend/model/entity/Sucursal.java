package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sucursal")
public class Sucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suc_id_int")
    private Integer id;

    @Column(name = "suc_desc_vac", length = 200)
    private String descripcion;

    @Column(name = "suc_direccion_vac", length = 300)
    private String direccion;

    @Column(name = "suc_telefono_vac")
    private String telefono;

    @Column(name = "suc_email_vac", length = 100)
    private String email;

    @Column(name = "suc_estado_bit")
    private Boolean estado;

    @CreationTimestamp
    @Column(name = "suc_fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "suc_fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
