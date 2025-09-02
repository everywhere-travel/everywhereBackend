package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Entity
@Table(name="viajeros_frecuente")
@Data
public class ViajeroFrecuente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="via_id_int")
    private Integer id;

    @Column(name="via_frec_aer_vac")
    private String areolinea;

    @Column(name="via_frec_cod_vac")
    private String codigo;

    @Column(name="via_cre_tmp", updatable = false)
    private LocalDateTime creado;

    @Column(name="via_upd_tmp")
    private LocalDateTime actualizado;

    // 👇 aquí estaba el error: estabas usando via_id_int otra vez
    @ManyToOne
    @JoinColumn(name="via_id_fk")
    private Viajero viajero;
}
