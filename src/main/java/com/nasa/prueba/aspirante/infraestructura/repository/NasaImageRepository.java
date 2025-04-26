package com.nasa.prueba.aspirante.infraestructura.repository;

import com.nasa.prueba.aspirante.dominio.entities.NasaImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface NasaImageRepository extends JpaRepository<NasaImageEntity, Long> {
    // Method to check if a record with the same NASA ID exists
    boolean existsByNasaId(String nasaId);
    
    // Optimized method to verify multiple IDs in a single query
    @Query("SELECT e.nasaId FROM NasaImageEntity e WHERE e.nasaId IN :nasaIds")
    List<String> findAllNasaIdsByNasaIdIn(@Param("nasaIds") Set<String> nasaIds);
}