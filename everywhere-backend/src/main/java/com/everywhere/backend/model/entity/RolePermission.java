package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(
    name = "role_permission",
    uniqueConstraints = @UniqueConstraint(
        name = "UQ_role_permission",
        columnNames = {"rol_id", "perm_id_int"}
    )
)
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_perm_id_int")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "perm_id_int", nullable = false)
    private Permission permission;

    @CreationTimestamp
    @Column(name = "rol_perm_cre_tmp", updatable = false)
    private LocalDateTime createdAt;
}
