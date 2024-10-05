package com.backend.dto.response.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.backend.entity.Image;
import com.backend.entity.Sku;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
	private Long id;
	private String name;
	private String slug;
	private Long categoryId;
	private Long brandId; 
    private List<SKUDTO> skus;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SKUDTO {
    	Long Id;
		Long price;
		Long stock;
		Long discount;
		String code; 
		List<Image> images;
		HashMap<String, String> attributes;
	}

}
