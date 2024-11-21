package com.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.constant.Type.OrderStatusType;
import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.request.order.OrderUpdateRequest;
import com.backend.dto.request.order.delivery.DeliveryRequest;
import com.backend.dto.request.order.payment.PaymentRequest;
import com.backend.dto.response.blog.BlogResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.entity.Blog;
import com.backend.entity.Delivery;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.entity.Payment;
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
import com.backend.repository.product.ProductRepository;
import com.backend.repository.SkuRepository;
import com.backend.repository.common.CustomSearchRepository;
import com.backend.repository.common.SearchType;
import com.backend.repository.order.OrderDetailRepository;
import com.backend.repository.order.OrderRepository;
import com.backend.repository.order.PaymentRepository;
import com.backend.repository.user.UserRepository;
import com.backend.utils.Helpers;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
	@Autowired
	OrderRepository orderRepository;
	EntityManager entityManager;
	DeliveryRepository deliveryRepository;
	OrderDetailRepository orderDetailRepository;
	PaymentRepository paymentRepository;

	public OrderResponse createOrder(OrderCreationRequest request) {
		Order order = new Order();

		if (request.getUserId() != null) {
			User user = userRepository.findById(request.getUserId())
					.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			order.setUser(user);
		}

		if (request.getDelivery() != null) {
			DeliveryRequest deliveryRequest = request.getDelivery();
			Delivery delivery = new Delivery();
			delivery.setUsername(deliveryRequest.getUsername());
			delivery.setAddress(deliveryRequest.getAddress());
			delivery.setEmail(deliveryRequest.getEmail());
			delivery.setNote(deliveryRequest.getNote());
			delivery.setNumberPhone(deliveryRequest.getNumberPhone());

			delivery = deliveryRepository.save(delivery); // Save delivery here
			order.setDelivery(delivery);
		}

		order.setStatus(request.getStatus());
		orderRepository.save(order); // Save Order here

		List<OrderDetail> orderDetails = Optional.ofNullable(request.getOrderDetails()).orElse(Collections.emptyList())
				.stream().map(detailRequest -> {
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

					return orderDetail;
				}).collect(Collectors.toList());

		orderDetailRepository.saveAll(orderDetails);

		double totalAmount = orderDetails.stream()
				.mapToDouble(orderDetail -> orderDetail.getPrice() * orderDetail.getQuantity()).sum();
		order.setTotal_amount(totalAmount);
		order.setOrderDetails(orderDetails);

		if (request.getPayment() != null) {
			PaymentOrder(request.getPayment(), order);
		}

		return orderMapper.toOrderResponse(orderRepository.save(order));
	}

	private void PaymentOrder(PaymentRequest paymentRequest, Order order) {
		Payment payment = Payment.builder().amount(paymentRequest.getAmount()).method(paymentRequest.getMethod())
				.status(paymentRequest.getStatus()).order(order).user(order.getUser()).build();

		paymentRepository.save(payment);
	}

	public PagedResponse<OrderResponse> getOrders(int page, int limit, String sort, String[] search,
			OrderStatusType status) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CustomSearchRepository<Order> customSearchService = new CustomSearchRepository<>(entityManager);

		CriteriaQuery<Order> query = customSearchService.buildSearchQuery(Order.class, search, sort);

		Root<Order> root = (Root<Order>) query.getRoots().iterator().next();

		if (status != null) {
			Predicate statusPredicate = criteriaBuilder.equal(root.get("status"), status);

			query.where(criteriaBuilder.and(query.getRestriction(), statusPredicate));
		}

		List<Order> orders = entityManager.createQuery(query).setFirstResult((page - 1) * limit).setMaxResults(limit)
				.getResultList();

		List<OrderResponse> orderResponses = orders.stream().map(orderMapper::toOrderResponse)
				.collect(Collectors.toList());

		// Truy vấn đếm số lượng bản ghi cho phân trang
		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Order.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(orderResponses, page, totalPages, totalElements, limit);
	}

	public OrderResponse updateOrder(Integer orderId, OrderUpdateRequest request) {

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
		double totalAmount = order.getOrderDetails().stream()
				.mapToDouble(orderDetail -> orderDetail.getPrice() * orderDetail.getQuantity()).sum();
		order.setTotal_amount(totalAmount);

		return orderMapper.toOrderResponse(orderRepository.save(order));

	}

	public void deleteOrder(Integer orderId) {
		orderRepository.deleteById(orderId);
	}

	public OrderResponse getOrderById(Integer orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

		return orderMapper.toOrderResponse(order);
	}

}
