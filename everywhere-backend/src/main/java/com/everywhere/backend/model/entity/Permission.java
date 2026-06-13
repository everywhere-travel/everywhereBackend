package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perm_id_int")
    private Integer id;

    // Formato: "MODULO:ACCION" — ej: "CLIENTES:READ", "ALL_MODULES:READ"
    @Column(name = "perm_name_vc", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "perm_desc_vc", length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "perm_cre_tmp", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "perm_upd_tmp")
    private LocalDateTime updatedAt;
}
