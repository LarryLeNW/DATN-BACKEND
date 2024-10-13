package com.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.backend.entity.Product;
import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.product.ProductUpdateRequest;
import com.backend.dto.response.product.ProductResponse;
import com.backend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Create Product
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("productData") String productData,
            @RequestPart("files") List<MultipartFile> files) {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductCreationRequest productRequest;

        try {
            productRequest = objectMapper.readValue(productData, ProductCreationRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Dữ liệu JSON không hợp lệ");
        }

        return ResponseEntity.ok(productService.createProduct(productRequest, files));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
    		@RequestPart("files") List<MultipartFile> files,
            @PathVariable Long id,
            @RequestPart("productData") String productData
            ) {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductUpdateRequest productRequest;

        try {
            productRequest = objectMapper.readValue(productData, ProductUpdateRequest.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        

        return ResponseEntity.ok(productService.updateProduct(id, productRequest, files));
    }

    // Get Products
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(@RequestParam Map<String, String> params,
                                                             @RequestParam(defaultValue = "0") int pageNumber,
                                                             @RequestParam(defaultValue = "10") int pageSize) {

        Page<ProductResponse> productsPage = productService.getProducts(params);
        return ResponseEntity.ok(productsPage);
    }

}
