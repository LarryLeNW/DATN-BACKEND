package com.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.brand.BrandCreationRequest;
import com.backend.dto.request.brand.BrandUpdateRequest;
import com.backend.dto.request.category.CategoryCreationRequest;
import com.backend.dto.request.category.CategoryUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.BrandMapper;
import com.backend.repository.BrandRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.utils.Helpers;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandService {

	BrandRepository brandRepository;
	BrandMapper brandMapper;
	EntityManager entityManager;
	UploadFile uploadFile;

	public PagedResponse<Brand> getBrands(int page, int limit, String sort, String... search) {
		List<SearchType> criteriaList = new ArrayList<>();
		CustomSearchRepository<Brand> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<Brand> query = customSearchService.buildSearchQuery(Brand.class, search, sort);

		List<Brand> brands = entityManager.createQuery(query).setFirstResult((page - 1) * limit).setMaxResults(limit)
				.getResultList();

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Brand.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(brands, page, totalPages, totalElements, limit);
	}

	public Brand createBrand(BrandCreationRequest request, MultipartFile image) {
		Brand brand = brandMapper.toBrand(request);
		brand.setSlug(Helpers.toSlug(request.getName()));

		if (image != null) {
			String imageUrl = uploadFile.saveFile(image, "brandTest");
			brand.setImage(imageUrl);
		}
		brandRepository.save(brand);
		return brand;
	}

	public Brand updateBrand(Long brandId, BrandUpdateRequest request, MultipartFile image) {
		Brand brand = brandRepository.findById(brandId)
				.orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));

		if (request != null) {
			Helpers.updateFieldEntityIfChanged(request.getName(), brand.getName(), brand::setName);
			Helpers.updateFieldEntityIfChanged(request.getDescription(), brand.getDescription(), brand::setDescription);

			if (request.getName() != null)
				brand.setSlug(Helpers.toSlug(request.getName()));
		}

		if (image != null) {
			String imageUrl = uploadFile.saveFile(image, "brandTest");
			brand.setImage(imageUrl);
		}

		return brandRepository.save(brand);
	}

	public void deleteBrand(Long brandId) {
		Brand brandFound = brandRepository.getById(brandId);
		if (brandFound == null)
			throw new RuntimeException("Không tìm thấy brand");
		brandRepository.delete(brandFound);
	}

}
