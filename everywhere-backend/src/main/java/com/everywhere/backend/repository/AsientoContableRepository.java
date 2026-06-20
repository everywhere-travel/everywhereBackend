package com.everywhere.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.everywhere.backend.model.entity.AsientoContable;

@Repository
public interface AsientoContableRepository 
        extends JpaRepository<AsientoContable, Integer> {

    List<AsientoContable> findByOrigenAndOrigenId(
            String origen, 
            Integer origenId
    );
}