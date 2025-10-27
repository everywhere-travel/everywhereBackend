package com.everywhere.backend.model.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prov_id_int")
    private int id;

    @Column(name = "prov_nomb_vac", length = 150)
    private String nombre;

    @Column(name = "prov_nom_jur_vac")
    private String nombreJuridico;

    @Column(name = "prov_ruc_int")
    private Integer ruc;

    @CreationTimestamp
    @Column(name = "prov_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "prov_upd_tmp")
    private LocalDateTime actualizado;
}
