package com.backend.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.product.ProductCreationRequest.SKUDTO;
import com.backend.dto.request.product.ProductUpdateRequest;
import com.backend.dto.response.blog.BlogResponse;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.product.ProductResponse;
import com.backend.entity.*;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.ProductMapper;
import com.backend.repository.*;
import com.backend.repository.product.AttributeOptionRepository;
import com.backend.repository.product.AttributeOptionSkuRepository;
import com.backend.repository.product.AttributeProductRepository;
import com.backend.repository.product.CategoryRepository;
import com.backend.repository.product.ProductRepository;
import com.backend.specification.ProductSpecification;
import com.backend.utils.UploadFile;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private BrandRepository brandRepository;

	@Autowired
	private AttributeProductRepository attributeRepository;

	@Autowired
	private AttributeOptionRepository attributeOptionRepository;

	@Autowired
	private SkuRepository skuRepository;

	@Autowired
	private AttributeOptionSkuRepository attributeOptionSkuRepository;

	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private UploadFile uploadFile;

	public ProductCreationRequest createProduct(ProductCreationRequest request) {
		Product productCreated = new Product();

		if (request.getCategoryId() != null) {
			productCreated.setCategory(categoryRepository.findById(request.getCategoryId())
					.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)));
		}

		if (request.getBrandId() != null) {
			productCreated.setBrand(brandRepository.findById(request.getBrandId())
					.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED)));
		}

		productCreated.setName(request.getName());
		productCreated.setSlug(request.getSlug());
		productCreated.setDescription(request.getDescription());

		productRepository.save(productCreated);

		Map<String, Attribute> attributeCache = new HashMap<>();
		Map<String, AttributeOption> attributeOptionCache = new HashMap<>();

		List<Sku> skusToSave = new ArrayList<>();
		List<AttributeOptionSku> attributeOptionSkusToSave = new ArrayList<>();

		for (ProductCreationRequest.SKUDTO skuDTO : request.getSkus()) {
			Sku skuCreated = new Sku(productCreated, skuDTO.getCode(), skuDTO.getPrice(), skuDTO.getStock(),
					skuDTO.getDiscount(), skuDTO.getImages());

			skusToSave.add(skuCreated);

			for (Map.Entry<String, String> entry : skuDTO.getAttributes().entrySet()) {
				String attributeName = entry.getKey();
				String attributeValue = entry.getValue();

				Attribute attribute = attributeCache.computeIfAbsent(attributeName, name -> {
					return attributeRepository.findByName(name).orElseGet(() -> {
						Attribute newAttribute = new Attribute();
						newAttribute.setName(name);
						return attributeRepository.save(newAttribute);
					});
				});

				String attributeOptionKey = attributeName + ":" + attributeValue;
				AttributeOption attributeOption = attributeOptionCache.computeIfAbsent(attributeOptionKey, key -> {
					return attributeOptionRepository.findByValueAndAttribute(attributeValue, attribute)
							.orElseGet(() -> {
								AttributeOption newAttributeOption = new AttributeOption();
								newAttributeOption.setValue(attributeValue);
								newAttributeOption.setAttribute(attribute);
								return attributeOptionRepository.save(newAttributeOption);
							});
				});

				AttributeOptionSkuKey attributeOptionSkuKey = new AttributeOptionSkuKey();
				attributeOptionSkuKey.setAttributeOptionId(attributeOption.getId());
				attributeOptionSkuKey.setSkuId(skuCreated.getId());

				AttributeOptionSku attributeOptionSku = new AttributeOptionSku();
				attributeOptionSku.setId(attributeOptionSkuKey);
				attributeOptionSku.setSku(skuCreated);
				attributeOptionSku.setAttributeOption(attributeOption);

				attributeOptionSkusToSave.add(attributeOptionSku);
			}

		}

		skuRepository.saveAll(skusToSave);
		attributeOptionSkuRepository.saveAll(attributeOptionSkusToSave);

		return request;
	}

	public ProductResponse updateProduct(Long productId, ProductUpdateRequest request, List<MultipartFile> images) {
		Product existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

		if (request.getCategoryId() != null) {
			existingProduct.setCategory(categoryRepository.findById(request.getCategoryId())
					.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)));
		}

		if (request.getBrandId() != null) {
			existingProduct.setBrand(brandRepository.findById(request.getBrandId())
					.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED)));
		}

		existingProduct.setName(request.getName());
		existingProduct.setSlug(request.getSlug());

		productRepository.save(existingProduct);

		Map<String, Attribute> attributeCache = new HashMap<>();
		Map<String, AttributeOption> attributeOptionCache = new HashMap<>();

		List<Sku> skusToSave = new ArrayList<>();
		List<AttributeOptionSku> attributeOptionSkusToSave = new ArrayList<>();

		List<Sku> existingSkus = skuRepository.findByProductId(productId);
		Map<Long, Sku> existingSkuMap = existingSkus.stream()
				.collect(Collectors.toMap(Sku::getId, sku -> sku));

		int imageIndex = 0;

		for (ProductUpdateRequest.SKUDTO skuDTO : request.getSkus()) {
			Sku sku;

			if (skuDTO.getId() != null && existingSkuMap.containsKey(skuDTO.getId())) {
				sku = existingSkuMap.get(skuDTO.getId());
				sku.setPrice(skuDTO.getPrice());
				sku.setStock(skuDTO.getStock());
				sku.setDiscount(skuDTO.getDiscount());
			} else {
				sku = new Sku(productId, existingProduct, skuDTO.getCode(), skuDTO.getPrice(), skuDTO.getStock(),
						skuDTO.getDiscount(), attributeOptionSkusToSave, null);
			}
			if (skuDTO.getId() != null && existingSkuMap.containsKey(skuDTO.getId())) {
				sku = existingSkuMap.get(skuDTO.getId());
				sku.setPrice(skuDTO.getPrice());
				sku.setStock(skuDTO.getStock());
				sku.setDiscount(skuDTO.getDiscount());
			} else {
				sku = new Sku(existingProduct, skuDTO.getCode(), skuDTO.getPrice(), skuDTO.getStock(),
						skuDTO.getDiscount(), null);
			}

			skusToSave.add(sku);

			StringBuilder imagesString = new StringBuilder();
			for (int i = 0; i < skuDTO.getImageCount(); i++) {
				if (imageIndex < images.size()) {
					MultipartFile imageFile = images.get(imageIndex);
					String imageUrl = uploadFile.saveFile(imageFile, "productTest");
					if (imagesString.length() > 0) {
						imagesString.append(",");
					}
					imagesString.append(imageUrl);
					imageIndex++;
				}
			}

			sku.setImages(imagesString.toString());
		}

		skuRepository.saveAll(skusToSave);
		attributeOptionSkuRepository.saveAll(attributeOptionSkusToSave);

		ProductResponse response = productMapper.toDTO(existingProduct);

		return response;
	}

	public PagedResponse<ProductResponse> getProducts(Map<String, String> params) {

		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;

		String sortField = params.getOrDefault("sortBy", "id");
		String orderBy = params.getOrDefault("orderBy", "asc");

		// handle sort
		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC
				: Sort.Direction.ASC;

		Sort sort = Sort.by(direction, sortField);

		Pageable pageable = PageRequest.of(page, limit, sort);

		Specification<Product> spec = Specification.where(null);

		if (params.containsKey("categoryId")) {
			Long categoryId = Long.parseLong(params.get("categoryId"));
			spec = spec.and(ProductSpecification.hasCategory(categoryId));
		}

		if (params.containsKey("price")) {
			Long price = Long.parseLong(params.get("price"));
			spec = spec.and(ProductSpecification.hasExactPrice(price));
		}

		if (params.containsKey("minPrice")) {
			Long price = Long.parseLong(params.get("minPrice"));
			spec = spec.and(ProductSpecification.hasMinPrice(price));
		}

		if (params.containsKey("maxPrice")) {
			Long price = Long.parseLong(params.get("maxPrice"));
			spec = spec.and(ProductSpecification.hasMaxPrice(price));
		}

		Map<String, String> attributes = params.entrySet().stream().filter(entry -> !List
				.of("page", "limit", "sortBy", "orderBy", "categoryId", "price", "minPrice", "maxPrice", "keyword")
				.contains(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (!attributes.isEmpty()) {
			spec = spec.and(ProductSpecification.hasAttributes(attributes));
		}

		Page<Product> productsPage = productRepository.findAll(spec, pageable);
		
		List<ProductResponse> productDTOs = productsPage.getContent().stream().map(productMapper::toDTO)
				.collect(Collectors.toList());

		return new PagedResponse<>(productDTOs, page + 1, productsPage.getTotalPages(), productsPage.getTotalElements(),
				limit);
	}
	
	
	@Transactional
	public String delete(Long productId) {
		productRepository.deleteById(productId);
		return "Deleted product successfully";
	}
	public ProductResponse getProductById(String id) {
	    Product product = productRepository.findById(Long.parseLong(id))
	            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

	    return productMapper.toDTO(product);
	}

}