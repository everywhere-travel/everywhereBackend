package com.everywhere.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.everywhere.backend.model.entity.CuentaContable;

@Repository
public interface CuentaContableRepository 
        extends JpaRepository<CuentaContable, Integer> {
}