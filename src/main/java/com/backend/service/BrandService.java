package com.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.dto.request.brand.BrandCreationRequest;
import com.backend.entity.Brand;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.BrandMapper;
import com.backend.repository.BrandRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandService {
	
	BrandRepository brandRepository;
	BrandMapper brandMapper;
	
	public List<Brand> getBrand(){
		try {
			List<Brand> brand = brandRepository.findAll();
			return brand;
		} catch (Exception e) {
			// TODO: handle exception
			throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
		}
	}
	public Brand createBrand (BrandCreationRequest request) {
		Brand brand = brandMapper.toBrand(request);		
		try {
		    //check for existence of the name
			if(brandRepository.existsByName(brand.getName())) {
				throw new AppException(ErrorCode.CATEGORY_NAME_EXISTED);
			}
			brandRepository.save(brand);
			
		} catch (Exception e) {
			// TODO: handle exception
			throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
		}
		return brand;
		
	}

}
