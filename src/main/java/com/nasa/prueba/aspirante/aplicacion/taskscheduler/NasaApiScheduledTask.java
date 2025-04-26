package com.nasa.prueba.aspirante.aplicacion.taskscheduler;

import com.nasa.prueba.aspirante.dominio.dto.NasaApiResponse;
import com.nasa.prueba.aspirante.dominio.entities.NasaImageEntity;
import com.nasa.prueba.aspirante.infraestructura.clientrest.NasaApiClient;
import com.nasa.prueba.aspirante.infraestructura.repository.NasaImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NasaApiScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(NasaApiScheduledTask.class);

    private final NasaApiClient nasaApiClient;
    private final NasaImageRepository nasaImageRepository;

    @Autowired
    public NasaApiScheduledTask(NasaApiClient nasaApiClient, NasaImageRepository nasaImageRepository) {
        this.nasaApiClient = nasaApiClient;
        this.nasaImageRepository = nasaImageRepository;
    }

    @Scheduled(fixedRate = 60000) // Execute every 1 minute (60,000 ms)
    public void fetchAndSaveNasaImages() {
        logger.info("Starting scheduled task to fetch NASA images");
        
        try {
            // Obtener la URL de la API de NASA a la que se hará la petición
            String apiUrl = nasaApiClient.buildApiUrl();
            logger.info("Haciendo petición a la URL: {}", apiUrl);
            
            NasaApiResponse response = nasaApiClient.fetchNasaImages();
            
            if (response != null && response.getCollection() != null && response.getCollection().getItems() != null) {
                logger.info("Retrieved {} items from NASA API", response.getCollection().getItems().length);
                
                for (NasaApiResponse.Item item : response.getCollection().getItems()) {
                    if (item.getData() != null && item.getData().length > 0) {
                        NasaApiResponse.NasaData nasaData = item.getData()[0];
                        
                        // Check if this NASA ID already exists in our database
                        if (!nasaImageRepository.existsByNasaId(nasaData.getNasa_id())) {
                            NasaImageEntity entity = new NasaImageEntity();
                            entity.setHref(item.getHref());
                            entity.setCenter(nasaData.getCenter());
                            entity.setTitle(nasaData.getTitle());
                            entity.setNasaId(nasaData.getNasa_id());
                            
                            nasaImageRepository.save(entity);
                            logger.info("Saved new NASA image: {}", nasaData.getTitle());
                        } else {
                            logger.info("NASA image already exists: {}", nasaData.getTitle());
                        }
                    }
                }
            } else {
                logger.warn("No data received from NASA API");
            }
        } catch (Exception e) {
            logger.error("Error fetching or processing NASA API data", e);
        }
    }
}