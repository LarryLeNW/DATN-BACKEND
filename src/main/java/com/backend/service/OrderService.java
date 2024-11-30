package com.backend.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
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
import com.backend.dto.request.order.OrderCreationRequest;
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

	private static Map<String, String> stage_zalo_config = new HashMap<String, String>() {
		{
			put("app_id", "2553");
			put("key1", "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL");
			put("key2", "kLtgPl8HHhfvMuDHPwKfgfsY4Ydm9eIz");
			put("endpoint", "https://sb-openapi.zalopay.vn/v2/create");
		}
	};

	static String createUrlPayment(double totalAmount, String app_trans_id)
			throws ClientProtocolException, IOException {

		Map<String, Object>[] item = new Map[] { new HashMap<>() {
			{
				put("itemid", "item001");
				put("itemname", "Product A");
				put("itemprice", totalAmount);
				put("itemquantity", 1);
			}
		} };

		Map<String, Object> embed_data = new HashMap<>();
		embed_data.put("merchantinfo", "Test Merchant");
		embed_data.put("redirecturl", "http://localhost:3000/checkout/payment/success");

		Map<String, Object> order = new HashMap<String, Object>() {
			{
				put("app_id", stage_zalo_config.get("app_id"));
				put("app_time", System.currentTimeMillis());
				put("app_trans_id", app_trans_id);
				put("app_user", "datn");
				put("amount", (int) totalAmount);
				put("description", "Payment for the order - DATN DEV TEAM 2025 DEMO");
				put("bank_code", "");
				put("item", new JSONArray(Arrays.asList(item)).toString());
				put("embed_data", new JSONObject(embed_data).toString());
				put("callback_url",
						"https://f5aa-2402-800-629c-f6b2-e84a-d4dd-8856-98bc.ngrok-free.app/api/payment/zalo/callback");
			}
		};

		String data = order.get("app_id") + "|" + order.get("app_trans_id") + "|" + order.get("app_user") + "|"
				+ order.get("amount") + "|" + order.get("app_time") + "|" + order.get("embed_data") + "|"
				+ order.get("item");
		String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, stage_zalo_config.get("key1"), data);
		order.put("mac", mac);

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(stage_zalo_config.get("endpoint"));

		List<NameValuePair> params = new ArrayList<>();
		for (Map.Entry<String, Object> e : order.entrySet()) {
			params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
		}

		post.setEntity(new UrlEncodedFormEntity(params));

		CloseableHttpResponse res = client.execute(post);

		BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
		StringBuilder resultJsonStr = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			resultJsonStr.append(line);
		}

		JSONObject result = new JSONObject(resultJsonStr.toString());

		if (result.has("order_url"))
			return result.getString("order_url");

		return null;
	}

	public String createOrder(OrderCreationRequest request) throws ClientProtocolException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		Order order = new Order();
		order.setUser(user);
		order.setOrderCode(request.getCode());
		order.setDiscountValue(request.getDiscountValue());
		
		Delivery delivery;

		if (request.getDelivery() != null) {
			Delivery deliveryCreated = deliveryMapper.toDelivery(request.getDelivery());
			deliveryCreated.setUser(user);
			delivery = deliveryRepository.save(deliveryCreated);
		} else {
			delivery = deliveryRepository.findFirstByUserAndIsDefaultTrue(user);
		}

		order.setDelivery(delivery);

		if (request.getPayment().getMethod() == PaymentMethod.COD) {
			order.setStatus(OrderStatusType.PENDING);
		}

		order.setTotal_amount(request.getPayment().getAmount());

		orderRepository.save(order);

		List<OrderDetail> orderDetails = Optional.ofNullable(request.getOrderDetails()).orElse(Collections.emptyList())
				.stream().map(detailRequest -> {
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

					if (detailRequest.getCart() != null) {
						cartRepository.delete(detailRequest.getCart());
					}

					return orderDetail;
				}).collect(Collectors.toList());

		orderDetailRepository.saveAll(orderDetails);

		order.setOrderDetails(orderDetails);

		String app_trans_id = Helpers.getCurrentTimeString("yyMMdd") + "_" + Integer.parseInt(Helpers.handleRandom(7));

		OrderStatusType orderStatus = request.getPayment().getMethod() != PaymentMethod.COD ? OrderStatusType.UNPAID
				: OrderStatusType.PENDING;

		order.setStatus(orderStatus);

		orderRepository.save(order);
		PaymentOrder(request.getPayment(), order, app_trans_id);

		if (request.getPayment().getMethod() == PaymentMethod.ZaloPay) {
			String url = createUrlPayment(request.getPayment().getAmount(), app_trans_id);
			log.info(url);
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

		if (params.containsKey("status")) {
			String status = params.get("status");
			spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
		}

		if ("ROLE_USER".equals(roleUser)) {
			spec = spec
					.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), idUser));
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
				if (!delivery.equals(order.getDelivery())) {
					order.setDelivery(delivery);
				}
			}
			Helpers.updateFieldEntityIfChanged(request.getTotalAmount(), order.getTotal_amount(),
					order::setTotal_amount);
			Helpers.updateFieldEntityIfChanged(request.getStatus(), order.getStatus(), order::setStatus);

			if (request.getOrderDetails() != null) {
				for (OrderDetailUpdateRequest detailRequest : request.getOrderDetails()) {
					Product product = productRepository.findById(detailRequest.getProductId())
							.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

					Sku sku = skuRepository.findById(detailRequest.getSkuid())
							.orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

					OrderDetail currentOrderDetail = orderDetailRepository.findById(detailRequest.getId())
							.orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED));

					OrderDetail foundOrderDetail = orderDetailRepository.findOneByOrderAndProductAndSku(order, product,
							sku);

					if (currentOrderDetail != null && foundOrderDetail.getId() != foundOrderDetail.getId()) {
						foundOrderDetail.setQuantity(foundOrderDetail.getQuantity() + detailRequest.getQuantity());

						orderDetailRepository.delete(currentOrderDetail);
						currentOrderDetail = foundOrderDetail;

					} else {
						currentOrderDetail.setQuantity(detailRequest.getQuantity());
						currentOrderDetail.setSku(sku);
						currentOrderDetail.setProduct(product);
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

		if(orderFound == null)
			throw new AppException(ErrorCode.ORDER_NOT_EXISTED);
		
		return orderMapper.toOrderResponse(orderFound);
	}

	public OrderResponse getOrderByCode(String orderId) {
		Order orderFound = orderRepository.findOneByOrderCode(orderId);

		if (orderFound == null)
			throw new RuntimeException("Not found info this order...");
		return orderMapper.toOrderResponse(orderFound);
	}

}
