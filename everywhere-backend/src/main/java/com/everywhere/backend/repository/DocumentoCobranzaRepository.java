package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DocumentoCobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoCobranzaRepository extends JpaRepository<DocumentoCobranza, Long> {

    /**
     * Busca el último número de documento de cobranza para generar el siguiente
     */
    @Query("SELECT MAX(d.numero) FROM DocumentoCobranza d WHERE d.numero LIKE 'DC01-%'")
    Optional<String> findLastDocumentNumber();

    Optional<DocumentoCobranza> findByNumero(String numero);

    @Query("SELECT d FROM DocumentoCobranza d WHERE d.persona.id = :personaId")
    Optional<DocumentoCobranza> findByPersonaId(Long personaId);
    
    @Query("SELECT d FROM DocumentoCobranza d WHERE d.cotizacion.id = :cotizacionId")
    Optional<DocumentoCobranza> findByCotizacionId(Integer cotizacionId);

    /**
     * Encuentra un documento por cotización ID con sus relaciones cargadas para evitar lazy loading
     */
    @Query("SELECT d FROM DocumentoCobranza d " +
           "LEFT JOIN FETCH d.persona p " +
           "LEFT JOIN FETCH d.sucursal " +
           "LEFT JOIN FETCH d.formaPago " +
           "LEFT JOIN FETCH d.cotizacion " +
           "LEFT JOIN FETCH d.detalles dt " +
           "LEFT JOIN FETCH dt.producto " +
           "WHERE d.cotizacion.id = :cotizacionId")
    Optional<DocumentoCobranza> findByCotizacionIdWithRelations(@Param("cotizacionId") Integer cotizacionId);

    /**
     * Encuentra todos los documentos con sus relaciones cargadas para evitar lazy loading
     */
    @Query("SELECT d FROM DocumentoCobranza d " +
           "LEFT JOIN FETCH d.persona p " +
           "LEFT JOIN FETCH d.sucursal " +
           "LEFT JOIN FETCH d.formaPago " +
           "LEFT JOIN FETCH d.cotizacion " +
           "LEFT JOIN FETCH d.detalles dt " +
           "LEFT JOIN FETCH dt.producto")
    List<DocumentoCobranza> findAllWithRelations();

    /**
     * Encuentra un documento por ID con sus relaciones cargadas para evitar lazy loading
     */
    @Query("SELECT d FROM DocumentoCobranza d " +
           "LEFT JOIN FETCH d.persona p " +
           "LEFT JOIN FETCH d.sucursal " +
           "LEFT JOIN FETCH d.formaPago " +
           "LEFT JOIN FETCH d.cotizacion " +
           "LEFT JOIN FETCH d.detalles dt " +
           "LEFT JOIN FETCH dt.producto " +
           "WHERE d.id = :id")
    Optional<DocumentoCobranza> findByIdWithRelations(@Param("id") Long id);

    /**
     * Encuentra un documento por número con sus relaciones cargadas para evitar lazy loading
     */
    @Query("SELECT d FROM DocumentoCobranza d " +
           "LEFT JOIN FETCH d.persona p " +
           "LEFT JOIN FETCH d.sucursal " +
           "LEFT JOIN FETCH d.formaPago " +
           "LEFT JOIN FETCH d.cotizacion " +
           "LEFT JOIN FETCH d.detalles dt " +
           "LEFT JOIN FETCH dt.producto " +
           "WHERE d.numero = :numero")
    Optional<DocumentoCobranza> findByNumeroWithRelations(@Param("numero") String numero);
}