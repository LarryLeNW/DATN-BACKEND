package com.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.request.order.OrderUpdateRequest;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.entity.Blog;
import com.backend.entity.Delivery;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.entity.Product;
import com.backend.entity.Reply;
import com.backend.entity.Sku;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.BlogMapper;
import com.backend.mapper.OrderMapper;
import com.backend.repository.BlogRepository;
import com.backend.repository.CategoryBlogRepository;
import com.backend.repository.DeliveryRepository;
import com.backend.repository.OrderRepository;
import com.backend.repository.ProductRepository;
import com.backend.repository.SkuRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.user.UserRepository;
import com.backend.utils.Helpers;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

	OrderMapper orderMapper;
	UserRepository userRepository;
	ProductRepository productRepository;
	SkuRepository skuRepository;
	OrderRepository orderRepository;
	EntityManager entityManager;
	DeliveryRepository deliveryRepository;

	public OrderResponse createOrder(OrderCreationRequest request) {
		Order order = new Order();

		if (request.getUserId() != null) {
			User user = userRepository.findById(request.getUserId())
					.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			order.setUser(user);
		}
		order.setTotal_amount(request.getTotalAmount());
		order.setStatus(request.getStatus());

		List<OrderDetail> orderDetails = request.getOrderDetails().stream().map(detailRequest -> {
			OrderDetail orderDetail = new OrderDetail();

			Product product = productRepository.findById(detailRequest.getProductId())
					.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
			orderDetail.setProduct(product);

			Sku sku = skuRepository.findById(detailRequest.getSkuid())
					.orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));
			orderDetail.setSku(sku);

			orderDetail.setQuantity(detailRequest.getQuantity());
			orderDetail.setPrice(detailRequest.getPrice());
			orderDetail.setOrder(order);
			orderDetail.setSku(sku);

			return orderDetail;
		}).collect(Collectors.toList());

		order.setOrderDetails(orderDetails);

		return orderMapper.toOrderResponse(orderRepository.save(order));
	}

	public PagedResponse<OrderResponse> getOrders(int page, int limit, String sort, String... search) {
		List<SearchType> criteriaList = new ArrayList<>();
		CustomSearchRepository<Order> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<Order> query = customSearchService.buildSearchQuery(Order.class, search, sort);

		List<Order> orders = entityManager.createQuery(query).setFirstResult((page - 1) * limit).setMaxResults(limit)
				.getResultList();

		List<OrderResponse> orderResponses = orders.stream().map(orderMapper::toOrderResponse)
				.collect(Collectors.toList());

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Order.class, search);
		
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(orderResponses, page, totalPages, totalElements, limit);
	}

	public OrderResponse updateOrder(String orderId, OrderUpdateRequest request) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

		if (request != null) {
			if (request.getDeliveryId() != null && !request.getDeliveryId().equals(order.getDelivery().getId())) {
				Delivery delivery = deliveryRepository.findById(Integer.parseInt(request.getDeliveryId()))
						.orElseThrow(() -> new AppException(ErrorCode.DELIVERY_NOT_EXISTED));
				order.setDelivery(delivery);
			}

			Helpers.updateFieldEntityIfChanged(request.getTotalAmount(), order.getTotal_amount(),
					order::setTotal_amount);
			Helpers.updateFieldEntityIfChanged(request.getStatus(), order.getStatus(), order::setStatus);

		}
		if (request.getOrderDetails() != null) {
			List<OrderDetail> updatedOrderDetails = request.getOrderDetails().stream().map(detailRequest -> {
				OrderDetail orderDetail = new OrderDetail();

				Product product = productRepository.findById(detailRequest.getProductId())
						.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
				orderDetail.setProduct(product);

				Sku sku = skuRepository.findById(detailRequest.getSkuid())
						.orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));
				orderDetail.setSku(sku);

				orderDetail.setQuantity(detailRequest.getQuantity());
				orderDetail.setOrder(order);
				return orderDetail;
			}).collect(Collectors.toList());

			order.setOrderDetails(updatedOrderDetails);
		}

		return orderMapper.toOrderResponse(orderRepository.save(order));

	}

	public void deleteOrder(String orderId) {
		orderRepository.deleteById(orderId);
	}

}
