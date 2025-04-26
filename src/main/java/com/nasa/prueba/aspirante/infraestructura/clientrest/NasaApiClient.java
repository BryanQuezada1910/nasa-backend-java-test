package com.nasa.prueba.aspirante.infraestructura.clientrest;

import com.nasa.prueba.aspirante.dominio.dto.NasaApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class NasaApiClient {

    private static final Logger logger = LoggerFactory.getLogger(NasaApiClient.class);

    @Value("${nasa.api.url}")
    private String nasaApiUrl;

    @Value("${nasa.api.search.param}")
    private String searchParam;

    private final RestTemplate restTemplate;

    public NasaApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public NasaApiResponse fetchNasaImages() {
        String url = buildApiUrl();
        try {
            logger.info("Realizando petición GET a: {}", url);
            
            // Usar ResponseEntity para obtener más información sobre la respuesta
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            
            logger.debug("Respuesta status code: {}", responseEntity.getStatusCodeValue());
            logger.debug("Respuesta headers: {}", responseEntity.getHeaders());
            
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                // Si la respuesta es exitosa, convertir el cuerpo a NasaApiResponse
                return restTemplate.getForObject(url, NasaApiResponse.class);
            } else {
                logger.error("Error en la respuesta API: Status={}, Body={}", 
                    responseEntity.getStatusCodeValue(), responseEntity.getBody());
                return null;
            }
        } catch (RestClientException e) {
            logger.error("Error al realizar la petición a la API de NASA: {}", e.getMessage(), e);
            return null;
        }
    }
    
    public String buildApiUrl() {
        // Usar encodedValue=false para evitar el doble encoding
        return UriComponentsBuilder.fromHttpUrl(nasaApiUrl)
                .queryParam("q", searchParam)
                .build(false)
                .toUriString();
    }
}