package com.backend.service.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.backend.dto.test.ProductDTO;
import com.backend.entity.test.Product;
import com.backend.mapper.test.ProductMapper;
import com.backend.repository.ProductRepository;
import com.backend.specification.ProductSpecification;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public Page<ProductDTO> getProducts(Long categoryId, Map<String, String> attributes, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);
        
        System.out.println(attributes.toString());
        
        if (categoryId != null) {
            spec = spec.and(ProductSpecification.hasCategory(categoryId));
        }

        if (attributes != null && !attributes.isEmpty()) {
            spec = spec.and(ProductSpecification.hasAttributes(attributes));
        }

        Page<Product> productsPage = productRepository.findAll(spec, pageable);
        List<ProductDTO> productDTOs = productsPage.getContent().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productsPage.getTotalElements());
    }

	

}
