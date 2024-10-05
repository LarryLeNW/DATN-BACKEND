package com.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.backend.dto.request.product.ProductDTO;
import com.backend.dto.request.product.ProductDTO.SKUDTO;
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

	public ProductDTO createProduct(ProductDTO request) {
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

		for (ProductDTO.SKUDTO skuDTO : request.getSkus()) {
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

		// Create response DTO
		ProductDTO response = new ProductDTO();
		response.setId(productCreated.getId());
		response.setName(productCreated.getName());
		response.setSlug(productCreated.getSlug());
		response.setCategoryId(productCreated.getCategory().getId());
		response.setBrandId(productCreated.getBrand().getId());

		// Map SKUs to DTO
		List<ProductDTO.SKUDTO> skuDTOs = new ArrayList<>();
		for (Sku sku : skusToSave) {
			ProductDTO.SKUDTO skuDTO = new ProductDTO.SKUDTO();
			skuDTO.setCode(sku.getCode());
			skuDTO.setPrice(sku.getPrice());
			skuDTO.setStock(sku.getStock());
			skuDTO.setDiscount(sku.getDiscount());

			// Set attributes for SKU
			Map<String, String> attributes = new HashMap<>();
			for (AttributeOptionSku attributeOptionSku : attributeOptionSkusToSave) {
				if (attributeOptionSku.getSku().getId().equals(sku.getId())) {
					String attributeName = attributeOptionSku.getAttributeOption().getAttribute().getName();
					String attributeValue = attributeOptionSku.getAttributeOption().getValue();
					attributes.put(attributeName, attributeValue);
				}
			}
			skuDTO.setAttributes(new HashMap<>(attributes));
			skuDTOs.add(skuDTO);
		}

		response.setSkus(skuDTOs);

		return response;
	}

	public ProductDTO updateProduct(Long productId, ProductDTO request) {
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

	    for (ProductDTO.SKUDTO skuDTO : request.getSkus()) {
	        Sku sku;

	        if (skuDTO.getId() != null && existingSkuMap.containsKey(skuDTO.getId())) {
	            // Update the existing SKU using the ID
	            sku = existingSkuMap.get(skuDTO.getId());
	            sku.setPrice(skuDTO.getPrice());
	            sku.setStock(skuDTO.getStock());
	            sku.setDiscount(skuDTO.getDiscount());
	        } else {
	            sku = new Sku(existingProduct, skuDTO.getCode(), skuDTO.getPrice(), skuDTO.getStock(), skuDTO.getDiscount());
	        }

	        skusToSave.add(sku);

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
	                return attributeOptionRepository.findByValueAndAttribute(attributeValue, attribute).orElseGet(() -> {
	                    AttributeOption newAttributeOption = new AttributeOption();
	                    newAttributeOption.setValue(attributeValue);
	                    newAttributeOption.setAttribute(attribute);
	                    return attributeOptionRepository.save(newAttributeOption);
	                });
	            });

	            AttributeOptionSkuKey attributeOptionSkuKey = new AttributeOptionSkuKey();
	            attributeOptionSkuKey.setAttributeOptionId(attributeOption.getId());
	            attributeOptionSkuKey.setSkuId(sku.getId());

	            Optional<AttributeOptionSku> existingAttributeOptionSku = attributeOptionSkuRepository.findById(attributeOptionSkuKey);
	            AttributeOptionSku attributeOptionSku;

	            if (existingAttributeOptionSku.isPresent()) {
	                attributeOptionSku = existingAttributeOptionSku.get();
	            } else {
	                attributeOptionSku = new AttributeOptionSku();
	                attributeOptionSku.setId(attributeOptionSkuKey);
	                attributeOptionSku.setSku(sku);
	                attributeOptionSku.setAttributeOption(attributeOption);
	            }

	            attributeOptionSkusToSave.add(attributeOptionSku);
	        }
	    }

	    skuRepository.saveAll(skusToSave);
	    attributeOptionSkuRepository.saveAll(attributeOptionSkusToSave);

	    ProductDTO response = productMapper.toDTO(existingProduct);

	    return response;
	}

	public Page<ProductDTO> getProducts(Map<String, String> params) {
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
		List<ProductDTO> productDTOs = productsPage.getContent().stream().map(productMapper::toDTO)
				.collect(Collectors.toList());

		return new PageImpl<>(productDTOs, pageable, productsPage.getTotalElements());
	}

}
