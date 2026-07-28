package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "configuracion_api")
public class ConfiguracionApi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conf_id_int")
    private Integer id;

    @Column(name = "conf_url_vac", length = 500)
    private String url;

    @Column(name = "conf_token_vac", length = 2000)
    private String token;

    @Column(name = "conf_act_bol")
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "conf_cre_dt", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "conf_upd_dt")
    private LocalDateTime fechaActualizacion;
}
