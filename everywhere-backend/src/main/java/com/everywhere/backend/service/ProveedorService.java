package com.everywhere.backend.service;

import com.everywhere.backend.model.entity.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProveedorService {

    List<Proveedor> findAll();

    Optional<Proveedor> findById(Integer id);

    Proveedor save(Proveedor proveedor);

    Proveedor update(Proveedor proveedor);

    void deleteById(Integer id);
}
