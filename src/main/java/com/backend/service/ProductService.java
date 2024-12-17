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
import com.backend.entity.rental.RentalPackage;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.ProductMapper;
import com.backend.mapper.RentalPackageMapper;
import com.backend.repository.*;
import com.backend.repository.product.AttributeOptionRepository;
import com.backend.repository.product.AttributeOptionSkuRepository;
import com.backend.repository.product.AttributeProductRepository;
import com.backend.repository.product.CategoryRepository;
import com.backend.repository.product.ProductRepository;
import com.backend.specification.ProductSpecification;
import com.backend.utils.UploadFile;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
	private RentalPackageMapper rentalPackageMapper;

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
		List<RentalPackage> rentalPackgages = rentalPackageMapper.toRentalPackages(request.getRentalPackages());
		for (RentalPackage rentalPackage : rentalPackgages) {
			rentalPackage.setProduct(productCreated);
		}
		
		productCreated.setRentalPackages(rentalPackgages);
		productRepository.save(productCreated);

		Map<String, Attribute> attributeCache = new HashMap<>();
		Map<String, AttributeOption> attributeOptionCache = new HashMap<>();

		List<Sku> skusToSave = new ArrayList<>();
		List<AttributeOptionSku> attributeOptionSkusToSave = new ArrayList<>();

		for (ProductCreationRequest.SKUDTO skuDTO : request.getSkus()) {
			Sku skuCreated = new Sku(productCreated, skuDTO.getCode(), skuDTO.getPrice(), skuDTO.getStock(),
					skuDTO.getDiscount(), skuDTO.getImages());
			skuCreated.setCanBeRented(skuDTO.getCanBeRented());
			
			if (skuDTO.getCanBeRented()) {
			    skuCreated.setHourlyRentPrice(skuDTO.getHourlyRentPrice());
			    skuCreated.setDailyRentPrice(skuDTO.getDailyRentPrice());
			    skuCreated.setMinRentalQuantity(skuDTO.getMinRentalQuantity());
			    skuCreated.setMaxRentalQuantity(skuDTO.getMaxRentalQuantity());
			}

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

	public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
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
		Map<Long, Sku> existingSkuMap = existingSkus.stream().collect(Collectors.toMap(Sku::getId, sku -> sku));

		for (ProductUpdateRequest.SKUDTO skuDTO : request.getSkus()) {
			Sku sku;

			if (skuDTO.getId() != null && existingSkuMap.containsKey(skuDTO.getId())) {
				sku = existingSkuMap.get(skuDTO.getId());
				sku.setPrice(skuDTO.getPrice());
				sku.setStock(skuDTO.getStock());
				sku.setDiscount(skuDTO.getDiscount());
				sku.setImages(skuDTO.getImages());
			} else {
				sku = new Sku(existingProduct, skuDTO.getCode(), skuDTO.getPrice(), skuDTO.getStock(),
						skuDTO.getDiscount(), skuDTO.getImages());
				log.info("skuDTO : " + skuDTO.toString());
			}

			skusToSave.add(sku);

			if (skuDTO.getAttributes() != null) {
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
					attributeOptionSkuKey.setSkuId(sku.getId());

					AttributeOptionSku attributeOptionSku = new AttributeOptionSku();
					attributeOptionSku.setId(attributeOptionSkuKey);
					attributeOptionSku.setSku(sku);
					attributeOptionSku.setAttributeOption(attributeOption);

					attributeOptionSkusToSave.add(attributeOptionSku);
				}
			}
		}

		Set<Long> skuIdsInRequest = request.getSkus().stream().map(ProductUpdateRequest.SKUDTO::getId)
				.filter(Objects::nonNull).collect(Collectors.toSet());

		for (Sku existingSku : existingSkus) {
			if (!skuIdsInRequest.contains(existingSku.getId())) {
				try {
					skuRepository.delete(existingSku);
				} catch (Exception e) {
					log.info("Can't not delete SKU with id " + existingSku.getId() + ": " + e.getMessage());
				}
			}
		}

		skuRepository.saveAll(skusToSave);
		attributeOptionSkuRepository.saveAll(attributeOptionSkusToSave);

		ProductResponse response = productMapper.toDTO(existingProduct);
		return response;
	}

	public PagedResponse<ProductResponse> getProducts(Map<String, String> params) {
		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;

		String sortField = params.getOrDefault("sortBy", "updatedAt");
		String orderBy = params.getOrDefault("orderBy", "desc");
		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;

		Specification<Product> spec = Specification.where(null);

		if (params.containsKey("keyword")) {
			String keyword = params.get("keyword").toLowerCase();
			spec = spec.and((root, query, builder) -> builder.or(
					builder.like(builder.lower(root.get("name")), "%" + keyword + "%"),
					builder.like(builder.lower(root.get("description")), "%" + keyword + "%")));
		}

		if (params.containsKey("category") && !params.get("category").isEmpty()) {
			spec = spec.and((root, query, criteriaBuilder) -> root.get("category").get("slug")
					.in(params.get("category").split(",")));
		}

		if (params.containsKey("brand")) {
			spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("brand").get("slug"),
					params.get("brand")));
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
		
	    if (params.containsKey("canBeRented") && Boolean.parseBoolean(params.get("canBeRented"))) {
	        spec = spec.and((root, query, criteriaBuilder) -> {
	            Join<Product, Sku> skuJoin = root.join("skus", JoinType.RIGHT);
	            return criteriaBuilder.equal(skuJoin.get("canBeRented"), true);
	        });
	    }
	    
		Map<String, String> attributes = params.entrySet().stream()
				.filter(entry -> !List.of("page", "limit", "sortBy", "orderBy", "category", "brand", "price",
						"minPrice", "maxPrice", "keyword", "stars", "canBeRented").contains(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		if (!attributes.isEmpty()) {
			spec = spec.and(ProductSpecification.hasAttributes(attributes));
		}

		if (params.containsKey("stars")) {
			Double minStars = Double.parseDouble(params.get("stars"));
			spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("stars"),
					minStars));
		}

		List<Product> productsList = productRepository.findAll(spec);

		Comparator<Product> comparator = getComparatorForSortField(sortField, direction);
		productsList.sort(comparator);

		int start = page * limit;
		int end = Math.min((start + limit), productsList.size());
		List<Product> pagedProducts = productsList.subList(start, end);

		List<ProductResponse> productDTOs = pagedProducts.stream().map(productMapper::toDTO)
				.collect(Collectors.toList());

		int totalPages = (int) Math.ceil((double) productsList.size() / limit);

		return new PagedResponse<>(productDTOs, page + 1, totalPages, productsList.size(), limit);
	}

	private Comparator<Product> getComparatorForSortField(String sortField, Sort.Direction direction) {
		switch (sortField) {
		case "stars":
			return direction == Sort.Direction.ASC ? Comparator.comparing(Product::getStars)
					: Comparator.comparing(Product::getStars, Comparator.reverseOrder());
		case "sold":
			return direction == Sort.Direction.ASC ? Comparator.comparing(Product::getTotalSold)
					: Comparator.comparing(Product::getTotalSold, Comparator.reverseOrder());
		case "price":
			return direction == Sort.Direction.ASC
					? Comparator.comparing(product -> product.getSkus().get(0).getPrice())
					: Comparator.comparing(product -> product.getSkus().get(0).getPrice(), Comparator.reverseOrder());
		case "discount":
			return direction == Sort.Direction.ASC
					? Comparator.comparing(product -> product.getSkus().get(0).getDiscount())
					: Comparator.comparing(product -> product.getSkus().get(0).getDiscount(),
							Comparator.reverseOrder());
		case "stock":
			return direction == Sort.Direction.ASC
					? Comparator.comparing(product -> product.getSkus().get(0).getStock())
					: Comparator.comparing(product -> product.getSkus().get(0).getStock(), Comparator.reverseOrder());
		default:
			return direction == Sort.Direction.ASC ? Comparator.comparing(Product::getId)
					: Comparator.comparing(Product::getId, Comparator.reverseOrder());
		}
	}

	public String delete(Long productId) {
		productRepository.deleteById(productId);
		return "Deleted product successfully";
	}

	public ProductResponse getProductById(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

		return productMapper.toDTO(product);
	}

}