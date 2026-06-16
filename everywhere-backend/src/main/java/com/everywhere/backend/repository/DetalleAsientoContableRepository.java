package com.everywhere.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.everywhere.backend.model.entity.DetalleAsientoContable;

@Repository
public interface DetalleAsientoContableRepository 
        extends JpaRepository<DetalleAsientoContable, Integer> {

    List<DetalleAsientoContable> findByAsientoId(Integer asientoId);
}