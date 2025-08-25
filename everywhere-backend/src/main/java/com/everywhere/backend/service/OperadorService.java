package com.everywhere.backend.service;

import com.everywhere.backend.model.entity.Operador;

import java.util.List;
import java.util.Optional;

public interface OperadorService {

    List<Operador> findAll();

    Optional<Operador> findByNombre(String nombre);

    Optional<Operador> findById(int id);

    Operador save(Operador operador);

    Operador update(Operador operador);

    void deleteById(int id);
}
