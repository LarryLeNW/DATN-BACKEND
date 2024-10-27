package com.backend.dto.request.product;

import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) 
public class ProductCreationRequest {
    private Long id;
    private String name;
    private String slug;
    private Long categoryId;
    private Long brandId; 
    private String thumbnailUrl;
    private List<SKUDTO> skus;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SKUDTO {
        private Long id;  
        private Long price;
        private Long stock;
        private Long discount;
        private String code; 
        private HashMap<String, String> attributes;
        private String images; 
    }
}
