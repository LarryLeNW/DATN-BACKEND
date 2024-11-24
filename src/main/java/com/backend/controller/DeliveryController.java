package com.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.delivery.DeliveryCreationRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.entity.Delivery;
import com.backend.repository.DeliveryRepository;
import com.backend.service.CommentService;
import com.backend.service.DeliveryService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/delivery")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DeliveryController {
	
	DeliveryService deliveryService;
	
	@PostMapping
	ApiResponse<Delivery> create(@RequestBody @Valid DeliveryCreationRequest request){
		return ApiResponse.<Delivery>builder().result(deliveryService.createDelivery(request)).build();
	}
	
	@GetMapping("/{id}")
	ApiResponse<Delivery> getOne(@PathVariable Integer id){
		return ApiResponse.<Delivery>builder().result(deliveryService.getOne(id)).build();
	}
	
	@PutMapping("/{id}")
	ApiResponse<Delivery> update(@RequestBody @Valid Delivery request,@PathVariable Integer id){
		return ApiResponse.<Delivery>builder().result(deliveryService.updateDelivery(request, id)).build();				
	}
	
	@GetMapping
	ApiResponse<PagedResponse<Delivery>> getAlls(
			@RequestParam Map<String, String> params) {
		return ApiResponse.<PagedResponse<Delivery>>builder().result(deliveryService.getAll(params)).build();
	}
	
	@DeleteMapping("/{id}")
	ApiResponse<String> delete(@PathVariable Integer id){
		deliveryService.deleteDelivery(id);
		return ApiResponse.<String>builder().result("Delete delivery successfully.").build();
	}
	
	
}
