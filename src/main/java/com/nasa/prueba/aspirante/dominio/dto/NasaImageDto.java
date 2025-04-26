package com.nasa.prueba.aspirante.dominio.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NasaImageDto {
    private Long id;
    private String href;
    private String center;
    private String title;
    private String nasaId;
    private LocalDateTime createdAt;
}