package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Personas;
import com.everywhere.backend.model.dto.PersonaTablaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Personas, Integer> {
    List<Personas> findByEmailContainingIgnoreCase(String email);
    @Query("""
       SELECT p 
       FROM Personas p 
       JOIN p.telefonos t 
       WHERE LOWER(t.numero) LIKE LOWER(CONCAT('%', :telefono, '%'))
       """)
    List<Personas> findByTelefonoContainingIgnoreCase(@Param("telefono") String telefono);

    @Query("SELECT p FROM Personas p LEFT JOIN FETCH p.telefonos WHERE p.id = :id")
    Optional<Personas> findByIdWithTelefonos(@Param("id") Integer id);

    @Query(value = """
        SELECT 
            p.per_id_int as id,
            CASE WHEN pn.per_nat_id_int IS NOT NULL THEN 'natural' ELSE 'juridica' END as tipo,
            CASE WHEN pn.per_nat_id_int IS NOT NULL THEN TRIM(CONCAT(COALESCE(pn.per_nat_nomb_vac, ''), ' ', COALESCE(pn.per_nat_apell_pat_vac, ''), ' ', COALESCE(pn.per_nat_apell_mat_vac, ''))) ELSE pj.per_jurd_raz_social_vac END as nombre,
            pn.per_nat_nomb_vac as nombres,
            pn.per_nat_apell_pat_vac as apellidosPaterno,
            pn.per_nat_apell_mat_vac as apellidosMaterno,
            pj.per_jurd_raz_social_vac as razonSocial,
            pn.per_nat_doc_int as documento,
            pj.per_jurd_ruc_int as ruc,
            (SELECT c.corre_emai_vac FROM correos_personas c WHERE c.corre_id_int = (SELECT MAX(c2.corre_id_int) FROM correos_personas c2 WHERE c2.per_id_int = p.per_id_int)) as email,
            (SELECT t.tel_num_vac FROM telefonos_personas t WHERE t.tel_id_int = (SELECT MAX(t2.tel_id_int) FROM telefonos_personas t2 WHERE t2.per_id_int = p.per_id_int)) as telefono,
            p.per_direc_vac as direccion
        FROM personas p
        LEFT JOIN persona_natural pn ON pn.per_id_int = p.per_id_int
        LEFT JOIN persona_juridica pj ON pj.per_id_int = p.per_id_int
        WHERE (CAST(:searchTerm AS text) IS NULL OR CAST(:searchTerm AS text) = '' OR 
               LOWER(CONCAT(COALESCE(pn.per_nat_nomb_vac, ''), ' ', COALESCE(pn.per_nat_apell_pat_vac, ''), ' ', COALESCE(pn.per_nat_apell_mat_vac, ''))) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS text), '%')) OR 
               LOWER(pj.per_jurd_raz_social_vac) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS text), '%')) OR 
               CAST(pn.per_nat_doc_int AS text) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS text), '%')) OR 
               CAST(pj.per_jurd_ruc_int AS text) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS text), '%')))
        AND (CAST(:typeFilter AS text) IS NULL OR CAST(:typeFilter AS text) = 'todos' OR 
             (CAST(:typeFilter AS text) = 'natural' AND pn.per_nat_id_int IS NOT NULL) OR 
             (CAST(:typeFilter AS text) = 'juridica' AND pn.per_nat_id_int IS NULL))
        """,
        countQuery = """
        SELECT COUNT(p.per_id_int) FROM personas p
        LEFT JOIN persona_natural pn ON pn.per_id_int = p.per_id_int
        LEFT JOIN persona_juridica pj ON pj.per_id_int = p.per_id_int
        WHERE (CAST(:searchTerm AS text) IS NULL OR CAST(:searchTerm AS text) = '' OR 
               LOWER(CONCAT(COALESCE(pn.per_nat_nomb_vac, ''), ' ', COALESCE(pn.per_nat_apell_pat_vac, ''), ' ', COALESCE(pn.per_nat_apell_mat_vac, ''))) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS text), '%')) OR 
               LOWER(pj.per_jurd_raz_social_vac) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS text), '%')) OR 
               CAST(pn.per_nat_doc_int AS text) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS text), '%')) OR 
               CAST(pj.per_jurd_ruc_int AS text) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS text), '%')))
        AND (CAST(:typeFilter AS text) IS NULL OR CAST(:typeFilter AS text) = 'todos' OR 
             (CAST(:typeFilter AS text) = 'natural' AND pn.per_nat_id_int IS NOT NULL) OR 
             (CAST(:typeFilter AS text) = 'juridica' AND pn.per_nat_id_int IS NULL))
        """,
        nativeQuery = true)
    Page<PersonaTablaDTO> findPersonasPage(@Param("searchTerm") String searchTerm, @Param("typeFilter") String typeFilter, Pageable pageable);
}