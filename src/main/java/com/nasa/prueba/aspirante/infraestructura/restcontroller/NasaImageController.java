package com.nasa.prueba.aspirante.infraestructura.restcontroller;

import com.nasa.prueba.aspirante.dominio.dto.NasaImageDto;
import com.nasa.prueba.aspirante.dominio.entities.NasaImageEntity;
import com.nasa.prueba.aspirante.infraestructura.repository.NasaImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nasa")
public class NasaImageController {

    private final NasaImageRepository nasaImageRepository;

    @Autowired
    public NasaImageController(NasaImageRepository nasaImageRepository) {
        this.nasaImageRepository = nasaImageRepository;
    }

    @GetMapping("/images")
    public ResponseEntity<List<NasaImageDto>> getAllNasaImages() {
        List<NasaImageEntity> entities = nasaImageRepository.findAll(
                Sort.by(Sort.Direction.DESC, "id"));
        
        List<NasaImageDto> dtos = entities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    private NasaImageDto convertToDto(NasaImageEntity entity) {
        NasaImageDto dto = new NasaImageDto();
        dto.setId(entity.getId());
        dto.setHref(entity.getHref());
        dto.setCenter(entity.getCenter());
        dto.setTitle(entity.getTitle());
        dto.setNasaId(entity.getNasaId());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}