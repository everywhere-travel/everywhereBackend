package com.everywhere.backend.service.impl;

import com.everywhere.backend.model.entity.Operador;
import com.everywhere.backend.repository.OperadorRepository;
import com.everywhere.backend.service.OperadorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository operadorRepository;

    public OperadorServiceImpl(OperadorRepository operadorRepository) {
        this.operadorRepository = operadorRepository;
    }

    @Override
    public List<Operador> findAll() {
        return operadorRepository.findAll();
    }

    @Override
    public Optional<Operador> findByNombre(String nombre) {
        return operadorRepository.findByNombre(nombre);
    }

    @Override
    public Optional<Operador> findById(int id) {
        return operadorRepository.findById(id);
    }

    @Override
    public Operador save(Operador operador) {
        return operadorRepository.save(operador);
    }

    @Override
    public Operador update(Operador operador) {
        Optional<Operador> optional = operadorRepository.findById(operador.getId());
        if (optional.isEmpty()) {
            throw new RuntimeException("Operador con id " + operador.getId() + " no encontrado");
        }
        return operadorRepository.save(operador);
    }

    @Override
    public void deleteById(int id) {
        operadorRepository.deleteById(id);
    }
}

