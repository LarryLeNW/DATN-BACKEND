package com.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.backend.dto.request.product.ProductDTO;
import com.backend.entity.Product;
import com.backend.mapper.ProductMapper;
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

	public ProductDTO createProduct(ProductDTO request) {
		
		
//	Product product = productMapper.toProduct(request);
//
//	Category category = categoryRepository.findById(request.getCategoryId())
//			.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
//
//	Brand brand = brandRepository.findById(request.getBrandId())
//			.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));
//
//	product.setCategory(category);
//	product.setBrand(brand);
//
//	Product productCreated = productRepository.save(product);
//
//	// Thêm lại thuộc tính từ request
//	if (request.getAttributes() != null && !request.getAttributes().isEmpty()) {
//		List<AttributeProduct> newAttributes = request.getAttributes().stream()
//				.map(attr -> new AttributeProduct(attr.getName(), attr.getValue())).collect(Collectors.toList());
//
//		// Lưu các thuộc tính mới vào product
//		product.setAttributes(newAttributes);
////        attributeProductRepository.saveAll(newAttributes);
//	}

	// Return the product response
	return request;
}
    
    
    
    public Page<ProductDTO> getProducts(Map<String, String> params) {
        // Xử lý pagination
        int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) : 0;
        int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
        String sortField = params.getOrDefault("sort", "id"); // Mặc định sort theo id nếu không có
        Sort sort = Sort.by(Sort.Direction.ASC, sortField);
        
        Pageable pageable = PageRequest.of(page, limit, sort);

        // Khởi tạo Specification cho bộ lọc
        Specification<Product> spec = Specification.where(null);

        // Lọc theo categoryId
        if (params.containsKey("categoryId")) {
            Long categoryId = Long.parseLong(params.get("categoryId"));
            spec = spec.and(ProductSpecification.hasCategory(categoryId));
        }

        // Lọc theo price
        if (params.containsKey("price")) {
            Long price = Long.parseLong(params.get("price"));
            spec = spec.and(ProductSpecification.hasExactPrice(price));
        }
        
        
        if (params.containsKey("minPrice")) {
        	Long price = Long.parseLong(params.get("minPrice"));
        	spec = spec.and(ProductSpecification.hasMinPrice(price));
        }
        
        if (params.containsKey("maxPrice")) {
        	Long price = Long.parseLong(params.get("minPrice"));
        	spec = spec.and(ProductSpecification.hasMaxPrice(price));
        }

        // Lọc theo các thuộc tính khác (tất cả những gì không phải là page, limit, sort, categoryId, price)
        Map<String, String> attributes = params.entrySet().stream()
                .filter(entry -> !List.of("page", "limit", "sort", "categoryId", "price", "minPrice", "maxPrice").contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!attributes.isEmpty()) {
            spec = spec.and(ProductSpecification.hasAttributes(attributes));
        }

        // Thực hiện tìm kiếm với bộ lọc và phân trang
        Page<Product> productsPage = productRepository.findAll(spec, pageable);
        List<ProductDTO> productDTOs = productsPage.getContent().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(productDTOs, pageable, productsPage.getTotalElements());
    }
}
