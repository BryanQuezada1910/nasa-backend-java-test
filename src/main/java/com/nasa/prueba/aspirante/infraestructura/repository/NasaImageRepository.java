package com.nasa.prueba.aspirante.infraestructura.repository;

import com.nasa.prueba.aspirante.dominio.entities.NasaImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NasaImageRepository extends JpaRepository<NasaImageEntity, Long> {
    // Method to check if a record with the same NASA ID exists
    boolean existsByNasaId(String nasaId);
}