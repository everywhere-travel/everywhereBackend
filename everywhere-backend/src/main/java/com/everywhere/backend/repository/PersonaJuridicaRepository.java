package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.PersonaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaJuridicaRepository extends JpaRepository<PersonaJuridica, Integer> {
    List<PersonaJuridica> findByRucContainingIgnoreCase(String ruc);
    List<PersonaJuridica> findByRazonSocialContainingIgnoreCase(String razonSocial);
}
