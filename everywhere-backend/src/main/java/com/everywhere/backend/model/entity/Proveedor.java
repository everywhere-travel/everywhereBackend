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
    private Long id;

    @Column(name = "prov_nomb_int", length = 150)
    private String nombre;

    @CreationTimestamp
    @Column(name = "prov_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    @Column(name = "prov_upd_tmp")
    private LocalDateTime actualizado;
}
