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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.constant.Type.OrderStatusType;
import com.backend.constant.Type.PaymentMethod;
import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.request.order.OrderDetailUpdateRequest;
import com.backend.dto.request.order.OrderUpdateRequest;
import com.backend.dto.request.order.delivery.DeliveryRequest;
import com.backend.dto.request.order.payment.PaymentRequest;
import com.backend.dto.response.blog.BlogResponse;
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

	static String createUrlPayment(double totalAmount) throws ClientProtocolException, IOException {
			
		Map<String, Object>[] item = new Map[] { new HashMap<>() {
			{
				put("itemid", "item001");
				put("itemname", "Product A");
				put("itemprice", totalAmount);
				put("itemquantity", 1);
			}
		} };

		int transition_id =  Integer.parseInt(Helpers.handleRandom(7));

		Map<String, Object> embed_data = new HashMap<>();
		embed_data.put("merchantinfo", "Test Merchant");
		embed_data.put("redirecturl", "http://facebook.com");

		Map<String, Object> order = new HashMap<String, Object>() {
			{
				put("app_id", stage_zalo_config.get("app_id"));
				put("app_time", System.currentTimeMillis());
				put("app_trans_id", Helpers.getCurrentTimeString("yyMMdd") + "_" + transition_id);
				put("app_user", "datn");
				put("amount", (int) totalAmount);
				put("description", "Payment for the order - DATN DEV TEAM 2025 DEMO" + transition_id);
				put("bank_code", "");
				put("item", new JSONArray(Arrays.asList(item)).toString());
				put("embed_data", new JSONObject(embed_data).toString());
				put("callback_url", "hacker.com");
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
		
		log.info(result.toString());
		
		if (result.has("order_url"))
			return result.getString("order_url");

		return null;
	}

	public String createOrder(OrderCreationRequest request) throws ClientProtocolException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		Order order = new Order();

		User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		order.setUser(user);
		order.setOrderCode(request.getCode());

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

		if (request.getPayment() != null) {
			PaymentOrder(request.getPayment(), order);
		}
		orderRepository.save(order);

		if (request.getPayment().getMethod() == PaymentMethod.ZaloPay) {
			String url = createUrlPayment(request.getPayment().getAmount());
			log.info(url);
			if (url != null)
				return url;
		}

		return request.getCode();
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

		CriteriaQuery<Long> countQuery = customSearchService.buildCountQuery(Order.class, search);
		long totalElements = entityManager.createQuery(countQuery).getSingleResult();

		int totalPages = (int) Math.ceil((double) totalElements / limit);

		return new PagedResponse<>(orderResponses, page, totalPages, totalElements, limit);
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
					System.out.println("hihi: " + foundOrderDetail.getId());
					System.out.println("hihi: " + foundOrderDetail.getProduct());
					System.out.println("hihi: " + foundOrderDetail.getSku());

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
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

		return orderMapper.toOrderResponse(order);
	}

	public OrderResponse getOrderByCode(String orderId) {
		Order orderFound = orderRepository.findOneByOrderCode(orderId);

		if (orderFound == null)
			throw new RuntimeException("Not found info this order...");
		return orderMapper.toOrderResponse(orderFound);
	}

}
