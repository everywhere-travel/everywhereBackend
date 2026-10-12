package com.everywhere.backend.audit.repository;

import com.everywhere.backend.audit.entity.Auditoria;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Especificación JPA dinámica para filtros de auditoría.
 * Evita la ambigüedad de tipos de parámetros nulos en PostgreSQL ('could not determine data type of parameter').
 */
public class AuditoriaSpecification {

    public static Specification<Auditoria> filterBy(
            String tabla,
            String accion,
            String username,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String searchTerm
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(tabla)) {
                predicates.add(cb.equal(cb.lower(root.get("tabla")), tabla.trim().toLowerCase()));
            }

            if (StringUtils.hasText(accion)) {
                predicates.add(cb.equal(cb.upper(root.get("accion")), accion.trim().toUpperCase()));
            }

            if (StringUtils.hasText(username)) {
                String pattern = "%" + username.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), pattern),
                        cb.like(cb.lower(root.get("usuarioNombre")), pattern)
                ));
            }

            if (fechaInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaHora"), fechaInicio));
            }

            if (fechaFin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaHora"), fechaFin));
            }

            if (StringUtils.hasText(searchTerm)) {
                String term = "%" + searchTerm.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("tabla")), term),
                        cb.like(cb.lower(root.get("registroId")), term),
                        cb.like(cb.lower(root.get("username")), term),
                        cb.like(cb.lower(root.get("usuarioNombre")), term),
                        cb.like(cb.lower(root.get("endpoint")), term)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
