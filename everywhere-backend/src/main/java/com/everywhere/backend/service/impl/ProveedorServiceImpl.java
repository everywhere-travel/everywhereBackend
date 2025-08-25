package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.entity.Proveedor;
import com.everywhere.backend.repository.ProveedorRepository;
import com.everywhere.backend.service.ProveedorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    @Override
    public Optional<Proveedor> findById(Integer id) {
        return proveedorRepository.findById(id);
    }

    @Override
    public Proveedor save(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor update(Proveedor proveedor) {
        Optional<Proveedor> optional = proveedorRepository.findById(proveedor.getId());
        if (optional.isEmpty()) {
            throw new RuntimeException("Proveedor con id " + proveedor.getId() + " no encontrado");
        }
        return proveedorRepository.save(proveedor);
    }


    @Override
    public void deleteById(Integer id) {
        proveedorRepository.deleteById(id);
    }

    }