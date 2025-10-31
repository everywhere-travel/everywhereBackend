package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.TelefonoPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelefonoPersonaRepository extends JpaRepository<TelefonoPersona, Integer> {

    // Buscar por número de teléfono (parcial o completo)
    List<TelefonoPersona> findByNumeroContaining(String numero);

    // Opcional: buscar por código de país
    List<TelefonoPersona> findByCodigoPais(String codigoPais);

    // Opcional: buscar por persona si quieres relacionar
    List<TelefonoPersona> findByPersonaId(Integer personaId);
}
