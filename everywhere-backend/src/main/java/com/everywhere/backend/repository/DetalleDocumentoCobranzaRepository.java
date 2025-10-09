package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DetalleDocumentoCobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleDocumentoCobranzaRepository extends JpaRepository<DetalleDocumentoCobranza, Long> {

    /**
     * Busca detalles por documento de cobranza
     */
    @Query("SELECT d FROM DetalleDocumentoCobranza d WHERE d.documentoCobranza.id = :documentoId")
    List<DetalleDocumentoCobranza> findByDocumentoCobranzaId(Long documentoId);

    /**
     * Busca detalles por producto
     */
    @Query("SELECT d FROM DetalleDocumentoCobranza d WHERE d.producto.id = :productoId")
    List<DetalleDocumentoCobranza> findByProductoId(Long productoId);
}