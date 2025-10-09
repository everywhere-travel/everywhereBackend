package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "documento_cobranza")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoCobranza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doc_co_id_int")
    private Long id;

    @Column(name = "doc_co_num_vac")
    private String numero;

    @Column(name = "doc_co_fec_emi_tmp")
    private LocalDateTime fechaEmision;

    @Column(name = "doc_co_obs_vac", columnDefinition = "TEXT")
    private String observaciones;

    // Relaciones con otras entidades
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carp_id_int")
    private Carpeta carpeta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id_int")
    private FormaPago formaPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usr_id_int")
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suc_id_int")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_id_int")
    private Personas persona;

    // Relación con detalles
    @OneToMany(mappedBy = "documentoCobranza", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleDocumentoCobranza> detalles;

    // Campos adicionales para datos de cotización (no mapeados a BD)
    @Transient
    private String nroSerie;

    @Transient
    private String fileVenta;

    @Transient
    private Double costoEnvio;

    @Transient
    private String moneda;

    @Transient
    private Long cotizacionId;
}
