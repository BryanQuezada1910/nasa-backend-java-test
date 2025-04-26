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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class NasaApiScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(NasaApiScheduledTask.class);

    private final NasaApiClient nasaApiClient;
    private final NasaImageRepository nasaImageRepository;
    
    // Successful execution counter
    private final AtomicInteger executionCount = new AtomicInteger(0);

    @Autowired
    public NasaApiScheduledTask(NasaApiClient nasaApiClient, NasaImageRepository nasaImageRepository) {
        this.nasaApiClient = nasaApiClient;
        this.nasaImageRepository = nasaImageRepository;
    }

    @Scheduled(fixedRate = 60000) // Execute every 1 minute (60,000 ms)
    @Transactional
    public void fetchAndSaveNasaImages() {
        logger.info("Starting scheduled task to fetch NASA images [Execution #{}]", 
            executionCount.incrementAndGet());
        
        try {
            // Get the NASA API URL to which the request will be made
            String apiUrl = nasaApiClient.buildApiUrl();
            logger.info("Making request to URL: {}", apiUrl);
            
            NasaApiResponse response = nasaApiClient.fetchNasaImages();
            
            if (response != null && response.getCollection() != null && response.getCollection().getItems() != null) {
                int totalItems = response.getCollection().getItems().length;
                logger.info("Retrieved {} items from NASA API", totalItems);
                
                // Use batch processing for better performance
                processItemsInBatches(response.getCollection().getItems());
            } else {
                logger.warn("No data received from NASA API or response structure is invalid");
            }
        } catch (Exception e) {
            logger.error("Error fetching or processing NASA API data: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Process items in batches for better performance
     */
    private void processItemsInBatches(NasaApiResponse.Item[] items) {
        final int BATCH_SIZE = 50; // Process in batches of 50 elements
        int totalItems = items.length;
        int newItemsCount = 0;
        int existingItemsCount = 0;
        
        // Collect all NASA IDs first for more efficient verification
        Set<String> existingNasaIds = new HashSet<>();
        for (NasaApiResponse.Item item : items) {
            if (item.getData() != null && item.getData().length > 0) {
                String nasaId = item.getData()[0].getNasa_id();
                if (nasaId != null) {
                    existingNasaIds.add(nasaId);
                }
            }
        }
        
        // Check which ones exist in the database (single query)
        Set<String> existingIdsInDb = new HashSet<>(
            nasaImageRepository.findAllNasaIdsByNasaIdIn(existingNasaIds));
        
        // Prepare entities to save
        List<NasaImageEntity> entitiesToSave = new ArrayList<>();
        
        for (NasaApiResponse.Item item : items) {
            if (item.getData() != null && item.getData().length > 0) {
                NasaApiResponse.NasaData nasaData = item.getData()[0];
                String nasaId = nasaData.getNasa_id();
                
                // Check if this NASA ID already exists in our database
                if (!existingIdsInDb.contains(nasaId)) {
                    NasaImageEntity entity = new NasaImageEntity();
                    entity.setHref(item.getHref());
                    entity.setCenter(nasaData.getCenter());
                    entity.setTitle(nasaData.getTitle());
                    entity.setNasaId(nasaData.getNasa_id());
                    
                    entitiesToSave.add(entity);
                    newItemsCount++;
                } else {
                    existingItemsCount++;
                }
                
                // Save in batches when reaching batch size or at the end
                if (entitiesToSave.size() >= BATCH_SIZE || 
                    (newItemsCount + existingItemsCount) == totalItems && !entitiesToSave.isEmpty()) {
                    nasaImageRepository.saveAll(entitiesToSave);
                    logger.info("Saved batch of {} NASA images", entitiesToSave.size());
                    entitiesToSave.clear();
                }
            }
        }
        
        logger.info("Processing complete. Total items: {}, New items: {}, Already existing: {}", 
            totalItems, newItemsCount, existingItemsCount);
    }
}