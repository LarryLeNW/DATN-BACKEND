package com.backend.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.constant.Type.OrderStatusType;
import com.backend.constant.Type.PaymentMethod;
import com.backend.constant.Type.PaymentStatus;
import com.backend.constant.Type.RentalStatus;
import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.request.order.OrderDetailCreationRequest;
import com.backend.dto.request.order.OrderDetailUpdateRequest;
import com.backend.dto.request.order.OrderUpdateRequest;
import com.backend.dto.request.order.delivery.DeliveryRequest;
import com.backend.dto.request.order.payment.PaymentRequest;
import com.backend.dto.response.blog.BlogResponse;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.entity.Blog;
import com.backend.entity.Cart;
import com.backend.entity.Delivery;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.entity.Payment;
import com.backend.entity.Product;
import com.backend.entity.Reply;
import com.backend.entity.Sku;
import com.backend.entity.User;
import com.backend.entity.rental.Rental;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.BlogMapper;
import com.backend.mapper.DeliveryMapper;
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
import com.backend.repository.user.CartRespository;
import com.backend.repository.user.UserRepository;
import com.backend.specification.CartSpecification;
import com.backend.utils.HMACUtil;
import com.backend.utils.Helpers;
import com.backend.utils.UploadFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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

	OrderDetailRepository orderDetailRepository;

	PaymentRepository paymentRepository;

	DeliveryMapper deliveryMapper;

	CartRespository cartRepository;

	PaymentService paymentService;

	public String createOrder(OrderCreationRequest requestData, HttpServletRequest request)
			throws ClientProtocolException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		Order order = new Order();
		order.setUser(user);
		order.setOrderCode(requestData.getCode());
		order.setDiscountValue(requestData.getDiscountValue());

		Delivery delivery;

		if (requestData.getDelivery() != null) {
			Delivery deliveryCreated = deliveryMapper.toDelivery(requestData.getDelivery());
			deliveryCreated.setUser(user);
			delivery = deliveryRepository.save(deliveryCreated);
		} else {
			delivery = deliveryRepository.findFirstByUserAndIsDefaultTrue(user);
		}

		order.setDelivery(delivery);

		if (requestData.getPayment().getMethod() == PaymentMethod.COD) {
			order.setStatus(OrderStatusType.PENDING);
		}

		order.setTotal_amount(requestData.getPayment().getAmount());

		orderRepository.save(order);

		List<OrderDetail> orderDetails = Optional.ofNullable(requestData.getOrderDetails())
				.orElse(Collections.emptyList()).stream().map(detailRequest -> {
					OrderDetail orderDetail = new OrderDetail();

					Product product = productRepository.findById(detailRequest.getProductId())
							.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
					orderDetail.setProduct(product);

					Sku sku = skuRepository.findById(detailRequest.getSkuId())
							.orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

					orderDetail.setSku(sku);
					orderDetail.setQuantity(detailRequest.getQuantity());
					orderDetail.setPrice(sku.getPrice());
					orderDetail.setOrder(order);
					orderDetail.setIsReview(false);

					if (detailRequest.getCart() != null) {
						cartRepository.delete(detailRequest.getCart());
					}

					return orderDetail;
				}).collect(Collectors.toList());

		orderDetailRepository.saveAll(orderDetails);

		order.setOrderDetails(orderDetails);

		String app_trans_id = Helpers.getCurrentTimeString("yyMMdd") + "_" + Integer.parseInt(Helpers.handleRandom(7));

		OrderStatusType orderStatus = requestData.getPayment().getMethod() != PaymentMethod.COD ? OrderStatusType.UNPAID
				: OrderStatusType.PENDING;

		order.setStatus(orderStatus);

		orderRepository.save(order);
		PaymentOrder(requestData.getPayment(), order, app_trans_id);

		if (requestData.getPayment().getMethod() == PaymentMethod.ZaloPay) {
			String url = paymentService.createPaymentZaloUrl(requestData.getPayment().getAmount(), app_trans_id,
					"https://f66a-113-166-213-84.ngrok-free.app/api/payment/zalo/callback",
					"http://localhost:3000/checkout/payment/success");
			if (url != null)
				return url;
		}

		if (requestData.getPayment().getMethod() == PaymentMethod.VNPay) {
			String url = paymentService.createVnPayUrl((int) requestData.getPayment().getAmount(), request,
					app_trans_id, "http://localhost:8080/api/payment/vn-pay/callback");
			if (url != null)
				return url;
		}

		return app_trans_id;
	}

	private void PaymentOrder(PaymentRequest paymentRequest, Order order, String app_trans_id) {
		Payment payment = Payment.builder().amount(paymentRequest.getAmount()).method(paymentRequest.getMethod())
				.status(paymentRequest.getStatus()).order(order).user(order.getUser()).appTransId(app_trans_id).build();
		paymentRepository.save(payment);
	}

	public PagedResponse<OrderResponse> getOrders(Map<String, String> params) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) - 1 : 0;
		int limit = params.containsKey("limit") ? Integer.parseInt(params.get("limit")) : 10;
		String sortField = params.getOrDefault("sortBy", "id");
		String orderBy = params.getOrDefault("orderBy", "desc");

		Sort.Direction direction = "desc".equalsIgnoreCase(orderBy) ? Sort.Direction.DESC : Sort.Direction.ASC;
		Sort sort = Sort.by(direction, sortField);
		Pageable pageable = PageRequest.of(page, limit, sort);

		Specification<Order> spec = Specification.where(null);

		if (params.containsKey("keyword")) {
			String keyword = params.get("keyword");
			spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.or(
					criteriaBuilder.like(root.get("orderCode"), "%" + keyword + "%"),
					criteriaBuilder.like(root.join("orderDetails").get("product").get("name"), "%" + keyword + "%"),
					criteriaBuilder.like(root.get("user").get("username"), "%" + keyword + "%")));
		}

		if (params.containsKey("status")) {
			String status = params.get("status");
			spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
		}

		if ("ROLE_USER".equals(roleUser)) {
			spec = spec
					.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), idUser));
		}

		if (params.containsKey("startDate") || params.containsKey("endDate")) {
			String startDateStr = params.get("startDate");
			String endDateStr = params.get("endDate");

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			final LocalDateTime startDate = (startDateStr != null)
					? LocalDate.parse(startDateStr, formatter).atStartOfDay()
					: null;
			final LocalDateTime endDate = (endDateStr != null)
					? LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX)
					: null;

			if (startDate != null && endDate != null) {
				spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdAt"),
						startDate, endDate));
			} else if (startDate != null) {
				spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
						.greaterThanOrEqualTo(root.get("createdAt"), startDate));
			} else if (endDate != null) {
				spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder
						.lessThanOrEqualTo(root.get("createdAt"), endDate));
			}
		}

		Page<Order> orderPage = orderRepository.findAll(spec, pageable);
		List<OrderResponse> cartResponses = orderPage.getContent().stream().map(orderMapper::toOrderResponse)
				.collect(Collectors.toList());

		return new PagedResponse<>(cartResponses, page + 1, orderPage.getTotalPages(), orderPage.getTotalElements(),
				limit);
	}

	@Transactional
	public OrderResponse updateOrder(Integer orderId, OrderUpdateRequest request) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

		if (request != null) {
			if (request.getDeliveryId() != null) {
				Delivery delivery = deliveryRepository.findById(Integer.parseInt(request.getDeliveryId()))
						.orElseThrow(() -> new AppException(ErrorCode.DELIVERY_NOT_EXISTED));
				Helpers.updateFieldEntityIfChanged(delivery, order.getDelivery(), order::setDelivery);
			}

			double totalAmount = order.getOrderDetails().stream().mapToDouble(od -> od.getPrice() * od.getQuantity())
					.sum();
			order.setTotal_amount(totalAmount);
//			Helpers.updateFieldEntityIfChanged(request.getTotalAmount(), order.getTotal_amount(),
//					order::setTotal_amount);
			Helpers.updateFieldEntityIfChanged(request.getStatus(), order.getStatus(), order::setStatus);

			if (request.getOrderDetails() != null) {
				for (OrderDetailUpdateRequest detailRequest : request.getOrderDetails()) {

					System.out.println("sl: " + detailRequest.getQuantity());

					Product product = productRepository.findById(detailRequest.getProductId())
							.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

					Sku sku = skuRepository.findById(detailRequest.getSkuid())
							.orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

					OrderDetail currentOrderDetail = orderDetailRepository.findById(detailRequest.getId())
							.orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED));

					OrderDetail foundOrderDetail = orderDetailRepository.findOneByOrderAndProductAndSku(order, product,
							sku);

					if (foundOrderDetail != null && !foundOrderDetail.getId().equals(currentOrderDetail.getId())) {

						foundOrderDetail.setQuantity(foundOrderDetail.getQuantity() + detailRequest.getQuantity());
						orderDetailRepository.delete(currentOrderDetail);
						currentOrderDetail = foundOrderDetail;
					} else {

						System.out.println("số lượng:" + detailRequest.getQuantity());
						currentOrderDetail.setSku(sku);
						currentOrderDetail.setProduct(product);
						currentOrderDetail.setQuantity(detailRequest.getQuantity());

					}

					orderDetailRepository.save(currentOrderDetail);

				}
			}
		}

		return orderMapper.toOrderResponse(orderRepository.save(order));
	}

	public void deleteOrder(Integer orderId) {
		orderRepository.deleteById(orderId);
	}

	public OrderResponse getOrderById(Integer orderId) {
		String idUser = SecurityContextHolder.getContext().getAuthentication().getName();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();

		Order orderFound = null;

		if ("ROLE_USER".equals(roleUser)) {
			User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			orderFound = orderRepository.findByIdAndUser(orderId, user);
		} else
			orderFound = orderRepository.findById(orderId)
					.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

		if (orderFound == null)
			throw new AppException(ErrorCode.ORDER_NOT_EXISTED);

		return orderMapper.toOrderResponse(orderFound);
	}

	public OrderResponse getOrderByCode(String orderId) {
		Order orderFound = orderRepository.findOneByOrderCode(orderId);

		if (orderFound == null)
			throw new RuntimeException("Not found info this order...");
		return orderMapper.toOrderResponse(orderFound);
	}

	@Transactional
	public void deleteOrderDetail(Integer orderDetailId) {
		OrderDetail orderDetailFound = orderDetailRepository.findById(orderDetailId)
				.orElseThrow(() -> new RuntimeException("not found"));

		Order order = orderDetailFound.getOrder();
		
		
		
		if (order != null) {
			order.getOrderDetails().remove(orderDetailFound);
			
			double totalAmount = order.getOrderDetails().stream().mapToDouble(od -> od.getPrice() * od.getQuantity()).sum();
			order.setTotal_amount(totalAmount);
		}

		orderDetailRepository.delete(orderDetailFound);
	}

	public OrderDetailResponse createOrderDetail(OrderDetailCreationRequest request) {

		System.out.println(request);

		OrderDetail orderDetail = new OrderDetail();

		if (request.getOrderId() != null) {
			Order order = orderRepository.findById(Integer.parseInt(request.getOrderId()))
					.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
			orderDetail.setOrder(order);

			if (request.getProductId() != null) {
				Product product = productRepository.findById((request.getProductId()))
						.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
				orderDetail.setProduct(product);
			}
			Sku sku = skuRepository.findById((request.getSkuId()))
					.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
			orderDetail.setSku(sku);
			orderDetail.setPrice(sku.getPrice());
			orderDetail.setQuantity(request.getQuantity());
			orderDetail.setIsReview(false);
			System.out.println(sku.getPrice());

			orderDetailRepository.save(orderDetail);

			double totalAmount = order.getOrderDetails().stream().mapToDouble(od -> od.getPrice() * od.getQuantity())
					.sum();
			order.setTotal_amount(totalAmount);

			orderRepository.save(order);
		}
		return orderMapper.toOrderDetailResponse(orderDetail);

	}

	public String updateStatus(Integer orderId, OrderStatusType status) {
		String idUser = SecurityContextHolder.getContext().getAuthentication().getName();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();

		Order orderFound = null;

		if ("ROLE_USER".equals(roleUser)) {
			User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
			orderFound = orderRepository.findByIdAndUser(orderId, user);
		} else
			orderFound = orderRepository.findById(orderId)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuê ..."));

		if (orderFound == null)
			throw new AppException(ErrorCode.ORDER_NOT_EXISTED);

		orderFound.setStatus(status);
		orderRepository.save(orderFound);

		return "Đã cập nhật đơn hàng";
	}

	public Map<OrderStatusType, Long> getOrderStatistics(Map<String, String> params) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> query = criteriaBuilder.createQuery(Object[].class);
		Root<Order> root = query.from(Order.class);

		Predicate predicate = criteriaBuilder.conjunction();

		if (params.containsKey("startDate") || params.containsKey("endDate")) {
			String startDateStr = params.get("startDate");
			String endDateStr = params.get("endDate");

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			final LocalDateTime startDate = (startDateStr != null)
					? LocalDate.parse(startDateStr, formatter).atStartOfDay()
					: null;
			final LocalDateTime endDate = (endDateStr != null)
					? LocalDate.parse(endDateStr, formatter).atTime(LocalTime.MAX)
					: null;

			if (startDate != null && endDate != null) {
				predicate = criteriaBuilder.and(predicate,
						criteriaBuilder.between(root.get("createdAt"), startDate, endDate));
			} else if (startDate != null) {
				predicate = criteriaBuilder.and(predicate,
						criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
			} else if (endDate != null) {
				predicate = criteriaBuilder.and(predicate,
						criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
			}
		}

		query.multiselect(root.get("status"), criteriaBuilder.count(root.get("id"))).where(predicate)
				.groupBy(root.get("status"));

		List<Object[]> results = entityManager.createQuery(query).getResultList();

		Map<OrderStatusType, Long> statistics = new HashMap<>();
		for (Object[] result : results) {
			OrderStatusType status = (OrderStatusType) result[0];
			Long count = (Long) result[1];
			statistics.put(status, count);
		}
		return statistics;
	}
	
	public Map<String, Long> getOrderTotals() {
	    LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
	    LocalDateTime endOfToday = LocalDateTime.now();

	    LocalDateTime startOfYesterday = LocalDate.now().minusDays(1).atStartOfDay();
	    LocalDateTime endOfYesterday = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);

	    LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
	    LocalDateTime startOfWeekTime = startOfWeek.atStartOfDay();
	    LocalDateTime endOfWeekTime = endOfToday;

	    LocalDate startOfYear = LocalDate.now().withDayOfYear(1);
	    LocalDateTime startOfYearTime = startOfYear.atStartOfDay();

	    long todayCount = orderRepository.countByCreatedAtBetween(startOfToday, endOfToday);
	    long yesterdayCount = orderRepository.countByCreatedAtBetween(startOfYesterday, endOfYesterday);
	    long thisWeekCount = orderRepository.countByCreatedAtBetween(startOfWeekTime, endOfWeekTime);
	    long thisYearCount = orderRepository.countByCreatedAtBetween(startOfYearTime, endOfToday);
	    long totalAllTime = orderRepository.count();

	    Map<String, Long> totals = new HashMap<>();
	    totals.put("today", todayCount);
	    totals.put("yesterday", yesterdayCount);
	    totals.put("thisWeek", thisWeekCount);
	    totals.put("thisYear", thisYearCount);
	    totals.put("allTime", totalAllTime);

	    return totals;
	}

	
	public Map<Integer, Long> getOrdersByDayInMonth(int month, int year) {
	    List<Object[]> results = orderRepository.countOrdersByDayInMonth(month, year);

	    Map<Integer, Long> ordersByDay = new LinkedHashMap<>();
	    for (int day = 1; day <= 31; day++) {
	        ordersByDay.put(day, 0L);
	    }

	    for (Object[] result : results) {
	        Integer day = (Integer) result[0];
	        Long count = (Long) result[1];
	        ordersByDay.put(day, count);
	    }

	    return ordersByDay;
	}


}
