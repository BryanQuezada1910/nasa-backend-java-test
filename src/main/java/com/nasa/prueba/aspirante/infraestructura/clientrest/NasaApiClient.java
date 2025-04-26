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

import java.util.Collections;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

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
            logger.info("Making GET request to: {}", url);
            
            // Configure headers
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            // Make API call and convert the result
            ResponseEntity<NasaApiResponse> responseEntity = 
                restTemplate.exchange(url, HttpMethod.GET, entity, NasaApiResponse.class);
            
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                logger.debug("Response status code: {}", responseEntity.getStatusCodeValue());
                logger.debug("Response headers: {}", responseEntity.getHeaders());
                
                return responseEntity.getBody();
            } else {
                logger.error("Error in API response: Status={}", 
                    responseEntity.getStatusCodeValue());
                return null;
            }
        } catch (RestClientException e) {
            logger.error("Error making request to NASA API: {}", e.getMessage(), e);
            return null;
        }
    }
    
    public String buildApiUrl() {
        // Use encodedValue=false to avoid double encoding
        return UriComponentsBuilder.fromHttpUrl(nasaApiUrl)
                .queryParam("q", searchParam)
                .build(false)
                .toUriString();
    }
}