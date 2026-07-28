package com.everywhere.backend.repository;

import com.everywhere.backend.model.entity.ConfiguracionApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionApiRepository extends JpaRepository<ConfiguracionApi, Integer> {
    Optional<ConfiguracionApi> findFirstByActivoTrueOrderByIdDesc();
}
