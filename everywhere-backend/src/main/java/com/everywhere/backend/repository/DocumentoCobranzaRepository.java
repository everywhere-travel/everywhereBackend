package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.DocumentoCobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentoCobranzaRepository extends JpaRepository<DocumentoCobranza, Long> {

    /**
     * Busca el último número de documento de cobranza para generar el siguiente
     */
    @Query("SELECT MAX(d.numero) FROM DocumentoCobranza d WHERE d.numero LIKE 'DC01-%'")
    Optional<String> findLastDocumentNumber();

    /**
     * Busca documento por número
     */
    Optional<DocumentoCobranza> findByNumero(String numero);

    /**
     * Busca documentos por persona
     */
    @Query("SELECT d FROM DocumentoCobranza d WHERE d.persona.id = :personaId")
    Optional<DocumentoCobranza> findByPersonaId(Long personaId);
}