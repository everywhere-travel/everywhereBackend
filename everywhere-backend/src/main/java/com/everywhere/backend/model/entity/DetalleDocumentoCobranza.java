package com.everywhere.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalle_documento_cobranza")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleDocumentoCobranza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "det_doc_co_id_int")
    private Long id;

    @Column(name = "det_doc_co_cant_int")
    private Integer cantidad;

    @Column(name = "det_doc_co_desc_vac", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "det_doc_co_prec_dc")
    private BigDecimal precio;

    @Column(name = "det_doc_co_cre_tmp")
    private LocalDateTime fechaCreacion;

    // Relaciones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_co_id_int")
    @JsonBackReference
    private DocumentoCobranza documentoCobranza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prod_id_int")
    private Producto producto;
}
