package com.nasa.prueba.aspirante.dominio.dto;

import lombok.Data;

@Data
public class NasaApiResponse {
    private Collection collection;

    @lombok.Data
    public static class Collection {
        private String version;
        private String href;
        private Item[] items;
        private Metadata metadata;
        private Links[] links;
    }

    @lombok.Data
    public static class Item {
        private String href;
        private NasaData[] data;
        private Link[] links;
    }

    @lombok.Data
    public static class NasaData {
        private String center;
        private String title;
        private String[] keywords;
        private String nasa_id;
        private String date_created;
        private String media_type;
        private String description;
        private String[] album;
        private String photographer;
        private String location;
    }

    @lombok.Data
    public static class Link {
        private String href;
        private String rel;
        private String render;
        private Integer width;
        private Integer height;
        private Integer size;
    }
    
    @lombok.Data
    public static class Links {
        private String href;
        private String rel;
        private String prompt;
    }

    @lombok.Data
    public static class Metadata {
        private int total_hits;
    }
}