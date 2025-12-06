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

       // Busca el último número de documento de cobranza para generar el siguiente
       @Query("SELECT MAX(d.numero) FROM DocumentoCobranza d WHERE d.numero LIKE 'DC01-%'")
       Optional<String> findLastDocumentNumber();

       @Query("SELECT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.carpeta " +
                     "LEFT JOIN FETCH d.formaPago " +
                     "LEFT JOIN FETCH d.usuario " +
                     "LEFT JOIN FETCH d.sucursal " +
                     "LEFT JOIN FETCH d.persona " +
                     "LEFT JOIN FETCH d.detalles " +
                     "LEFT JOIN FETCH d.cotizacion " +
                     "WHERE d.numero = :numero")
       Optional<DocumentoCobranza> findByNumero(@Param("numero") String numero);

       @Query("SELECT d FROM DocumentoCobranza d WHERE d.persona.id = :personaId")
       Optional<DocumentoCobranza> findByPersonaId(@Param("personaId") Long personaId);

       @Query("SELECT d FROM DocumentoCobranza d WHERE d.cotizacion.id = :cotizacionId")
       Optional<DocumentoCobranza> findByCotizacionId(@Param("cotizacionId") Integer cotizacionId);

       @Query("SELECT DISTINCT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.carpeta " +
                     "LEFT JOIN FETCH d.formaPago fp " +
                     "LEFT JOIN FETCH d.usuario " +
                     "LEFT JOIN FETCH d.sucursal " +
                     "LEFT JOIN FETCH d.persona " +
                     "LEFT JOIN FETCH d.personaJuridica " +
                     "LEFT JOIN FETCH d.detalleDocumento " +
                     "LEFT JOIN FETCH d.cotizacion " +
                     "WHERE d.id = :id")
       Optional<DocumentoCobranza> findByIdWithRelations(@Param("id") Long id);

       @Query("SELECT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.carpeta " +
                     "LEFT JOIN FETCH d.formaPago " +
                     "LEFT JOIN FETCH d.usuario " +
                     "LEFT JOIN FETCH d.sucursal " +
                     "LEFT JOIN FETCH d.persona " +
                     "LEFT JOIN FETCH d.cotizacion")
       List<DocumentoCobranza> findAllWithRelations();

       @Query("SELECT DISTINCT d FROM DocumentoCobranza d " +
                     "LEFT JOIN FETCH d.detalles det " +
                     "LEFT JOIN FETCH det.producto " +
                     "WHERE d.id = :id")
       Optional<DocumentoCobranza> findByIdWithDetalles(@Param("id") Long id);
}