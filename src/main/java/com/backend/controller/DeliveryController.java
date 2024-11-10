package com.backend.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.request.reply.ReplyCreationRequest;
import com.backend.dto.request.reply.ReplyUpdateRequest;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.reply.ReplyResponse;
import com.backend.entity.Delivery;
import com.backend.repository.DeliveryRepository;
import com.backend.service.CommentService;
import com.backend.service.DeliveryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/deliverys")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DeliveryController {
	
	DeliveryService deliveryService;
	
	@PostMapping
	ApiResponse<Delivery> createReply (@RequestBody Delivery Delivery){
		return ApiResponse.<Delivery>builder().result(deliveryService.createDelivery(Delivery)).build();
	}
	
	@PutMapping("/{id}")
	ApiResponse<Delivery> updateComment(@RequestBody Delivery request,@PathVariable Integer id){
		return ApiResponse.<Delivery>builder().result(deliveryService.updateDelivery(request, id)).build();				
	}
	@DeleteMapping("/{id}")
	ApiResponse<String> deleteComment (@PathVariable Integer id){
		deliveryService.deleteDelivery(id);
		return ApiResponse.<String>builder().result("delete success deliverys ").build();
	}
	@GetMapping("/{id}")
	ApiResponse<Delivery> getDeliveryById (@PathVariable Integer id){
		return ApiResponse.<Delivery>builder().result(deliveryService.getDeliveryById(id)).build();

	}

	
}
