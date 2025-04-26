package com.nasa.prueba.aspirante.dominio.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nasa_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NasaImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String href;

    @Column(nullable = false)
    private String center;

    @Column(nullable = false)
    private String title;

    @Column(name = "nasa_id", nullable = false)
    private String nasaId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}