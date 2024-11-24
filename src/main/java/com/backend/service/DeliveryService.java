package com.backend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.backend.dto.request.delivery.DeliveryCreationRequest;
import com.backend.dto.request.delivery.DeliveryUpdateRequest;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Cart;
import com.backend.entity.Delivery;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.DeliveryMapper;
import com.backend.repository.DeliveryRepository;
import com.backend.repository.user.UserRepository;
import com.backend.specification.CartSpecification;
import com.backend.utils.Helpers;

import jakarta.transaction.Transactional;

@Service
public class DeliveryService {

	@Autowired
	DeliveryRepository deliveryRepository;
	@Autowired
	UserRepository userRepository;

	@Autowired
	DeliveryMapper deliveryMapper;

	public Delivery createDelivery(DeliveryCreationRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userId = auth.getName();
		User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		long deliveryCount = deliveryRepository.countByUser(user);

		if (deliveryCount == 0) {
			request.setIsDefault(true);
		} else if (Boolean.TRUE.equals(request.getIsDefault())) {
			Delivery currentDefault = deliveryRepository.findFirstByUserAndIsDefaultTrue(user);
			if (currentDefault != null) {
				currentDefault.setIsDefault(false);
				deliveryRepository.save(currentDefault);
			}
		}

		Delivery delivery = deliveryMapper.toDelivery(request);

		delivery.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()));
		delivery.setUser(user);

		return deliveryRepository.save(delivery);
	}

	public PagedResponse<Delivery> getAll(Map<String, String> params) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
		String sortField = params.getOrDefault("sortBy", "id");
		String orderBy = params.getOrDefault("orderBy", "asc");

		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, sortField);
		Pageable pageable = PageRequest.of(page, limit, sort);

		Specification<Delivery> spec = Specification.where(null);

		if ("ROLE_USER".equals(roleUser)) {
			spec = spec
					.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), idUser));
		}

		Page<Delivery> deliveryPage = deliveryRepository.findAll(spec, pageable);

		return new PagedResponse<>(deliveryPage.getContent(), page + 1, deliveryPage.getTotalPages(),
				deliveryPage.getTotalElements(), limit);
	}

	public Delivery getOne(Integer id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		Delivery deliveryFound = null;

		if ("ROLE_USER".equals(roleUser)) {
			User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			deliveryFound = deliveryRepository.findByIdAndUser(id, user);

			if (deliveryFound == null) {
				throw new AppException(ErrorCode.DELIVERY_NOT_EXISTED);
			}
		} else {
			deliveryFound = deliveryRepository.findById(id)
					.orElseThrow(() -> new AppException(ErrorCode.DELIVERY_NOT_EXISTED));
		}

		return deliveryFound;
	}

	@Transactional
	public void deleteDelivery(Integer id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();
		if ("ROLE_USER".equals(roleUser)) {
			User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			deliveryRepository.deleteByIdAndUser(id, user);
		} else {
			deliveryRepository.deleteById(id);
		}

	}

	public Delivery updateDelivery(Delivery request, Integer id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		Delivery deliveryFound = null;

		if ("ROLE_USER".equals(roleUser)) {
			User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			deliveryFound = deliveryRepository.findByIdAndUser(id, user);

			if (deliveryFound == null) {
				throw new AppException(ErrorCode.DELIVERY_NOT_EXISTED);
			}

			if (Boolean.TRUE.equals(request.getIsDefault())) {
				Delivery currentDefault = deliveryRepository.findFirstByUserAndIsDefaultTrue(user);
				if (currentDefault != null) {
					currentDefault.setIsDefault(false);
					deliveryRepository.save(currentDefault);
				}
			}

		} else {
			deliveryFound = deliveryRepository.findById(id)
					.orElseThrow(() -> new AppException(ErrorCode.DELIVERY_NOT_EXISTED));
		}

		Helpers.updateFieldEntityIfChanged(request.getUsername(), deliveryFound.getUsername(),
				deliveryFound::setUsername);

		Helpers.updateFieldEntityIfChanged(request.getCity(), deliveryFound.getCity(), deliveryFound::setCity);
		Helpers.updateFieldEntityIfChanged(request.getCity_id(), deliveryFound.getCity_id(), deliveryFound::setCity_id);

		Helpers.updateFieldEntityIfChanged(request.getDistrict(), deliveryFound.getDistrict(),
				deliveryFound::setDistrict);
		Helpers.updateFieldEntityIfChanged(request.getDistrict_id(), deliveryFound.getDistrict_id(),
				deliveryFound::setDistrict_id);

		Helpers.updateFieldEntityIfChanged(request.getWard(), deliveryFound.getWard(), deliveryFound::setWard);
		Helpers.updateFieldEntityIfChanged(request.getWard_id(), deliveryFound.getWard_id(), deliveryFound::setWard_id);

		Helpers.updateFieldEntityIfChanged(request.getNumberPhone(), deliveryFound.getNumberPhone(),
				deliveryFound::setNumberPhone);

		Helpers.updateFieldEntityIfChanged(request.getIsDefault(), deliveryFound.getIsDefault(),
				deliveryFound::setIsDefault);

		Helpers.updateFieldEntityIfChanged(request.getCompany_name(), deliveryFound.getCompany_name(),
				deliveryFound::setCompany_name);

		Helpers.updateFieldEntityIfChanged(request.getStreet(), deliveryFound.getStreet(), deliveryFound::setStreet);

		Helpers.updateFieldEntityIfChanged(request.getTypeAddress(), deliveryFound.getTypeAddress(),
				deliveryFound::setTypeAddress);

		return deliveryRepository.save(deliveryFound);
	}

}
