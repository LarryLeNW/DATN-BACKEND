package com.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.backend.dto.request.product.ProductCreationRequest;
import com.backend.dto.request.product.ProductCreationRequest.SKUDTO;
import com.backend.dto.response.product.ProductResponse;
import com.backend.entity.*;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.ProductMapper;
import com.backend.repository.*;
import com.backend.specification.ProductSpecification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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

		productRepository.save(productCreated);

		Map<String, Attribute> attributeCache = new HashMap<>();
		Map<String, AttributeOption> attributeOptionCache = new HashMap<>();

		List<Sku> skusToSave = new ArrayList<>();
		List<AttributeOptionSku> attributeOptionSkusToSave = new ArrayList<>();

		for (ProductCreationRequest.SKUDTO skuDTO : request.getSkus()) {
			Sku skuCreated = new Sku(productCreated, skuDTO.getCode(), skuDTO.getPrice(), skuDTO.getStock(),
					skuDTO.getDiscount());
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

	public ProductResponse updateProduct(Long productId, ProductCreationRequest request) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

		// Update basic product information
		product.setName(request.getName());
		product.setSlug(request.getSlug());
		
		if (request.getCategoryId() != null) {
			product.setCategory(categoryRepository.findById(request.getCategoryId())
					.orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)));
		}
		if (request.getBrandId() != null) {
			product.setBrand(brandRepository.findById(request.getBrandId())
					.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED)));
		}

		// Create a map of existing SKUs for easy lookup
		Map<Long, Sku> existingSkus = product.getSkus().stream().collect(Collectors.toMap(Sku::getId, sku -> sku));

		List<Sku> updatedSkus = new ArrayList<>();

		for (ProductCreationRequest.SKUDTO skuDTO : request.getSkus()) {
			Sku skuToUpdate = existingSkus.get(skuDTO.getId());

			if (skuToUpdate != null) {
				// Update the existing SKU
				skuToUpdate.setPrice(skuDTO.getPrice());
				skuToUpdate.setStock(skuDTO.getStock());
				skuToUpdate.setDiscount(skuDTO.getDiscount());
				skuToUpdate.setCode(skuDTO.getCode());

				// Update attributes
				Map<String, String> attributes = skuDTO.getAttributes();
				List<AttributeOptionSku> attributeOptionSkus = skuToUpdate.getAttributeOptionSkus();

				for (AttributeOptionSku attributeOptionSku : attributeOptionSkus) {
					String attributeName = attributeOptionSku.getAttributeOption().getAttribute().getName();
					if (attributes.containsKey(attributeName)) {
						attributeOptionSku.getAttributeOption().setValue(attributes.get(attributeName));
					}
				}
				updatedSkus.add(skuToUpdate);
			} else {
				// Create a new SKU
				Sku newSku = new Sku(product, skuDTO.getCode(), skuDTO.getPrice(), skuDTO.getStock(),
						skuDTO.getDiscount());

				// Create and assign new attributes
				Map<String, Attribute> attributeCache = new HashMap<>();
				Map<String, AttributeOption> attributeOptionCache = new HashMap<>();
				List<AttributeOptionSku> attributeOptionSkus = new ArrayList<>();

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
					attributeOptionSkuKey.setSkuId(newSku.getId());

					AttributeOptionSku attributeOptionSku = new AttributeOptionSku();
					attributeOptionSku.setId(attributeOptionSkuKey);
					attributeOptionSku.setSku(newSku);
					attributeOptionSku.setAttributeOption(attributeOption);

					attributeOptionSkus.add(attributeOptionSku);
				}
				newSku.setAttributeOptionSkus(attributeOptionSkus);
				updatedSkus.add(newSku);
			}
		}

		product.setSkus(updatedSkus);

		productRepository.save(product);

		return productMapper.toDTO(product);
	}

	public Page<ProductResponse> getProducts(Map<String, String> params) {
		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
		String sortField = params.getOrDefault("sort", "id"); // Mặc định sort theo id nếu không có
		Sort sort = Sort.by(Sort.Direction.ASC, sortField);

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
				.of("page", "limit", "sort", "categoryId", "price", "minPrice", "maxPrice").contains(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (!attributes.isEmpty()) {
			spec = spec.and(ProductSpecification.hasAttributes(attributes));
		}

		Page<Product> productsPage = productRepository.findAll(spec, pageable);
		List<ProductResponse> productDTOs = productsPage.getContent().stream().map(productMapper::toDTO)
				.collect(Collectors.toList());

		return new PageImpl<>(productDTOs, pageable, productsPage.getTotalElements());
	}

}
