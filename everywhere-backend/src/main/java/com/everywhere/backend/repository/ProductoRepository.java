package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Optional<Producto> findByCodigo(String codigo); // 👈 nuevo

}
