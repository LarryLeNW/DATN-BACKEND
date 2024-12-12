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
import com.backend.constant.Type.RentalStatus;
import com.backend.dto.request.order.OrderCreationRequest;
import com.backend.dto.request.order.OrderDetailUpdateRequest;
import com.backend.dto.request.order.OrderUpdateRequest;
import com.backend.dto.request.order.delivery.DeliveryRequest;
import com.backend.dto.request.order.payment.PaymentRequest;
import com.backend.dto.request.rental.RentalCreation;
import com.backend.dto.response.blog.BlogResponse;
import com.backend.dto.response.cart.CartDetailResponse;
import com.backend.dto.response.common.PagedResponse;
import com.backend.dto.response.order.OrderDetailResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.dto.response.rental.RentalResponse;
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
import com.backend.entity.rental.RentalDetail;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.mapper.BlogMapper;
import com.backend.mapper.DeliveryMapper;
import com.backend.mapper.OrderMapper;
import com.backend.mapper.RentalMapper;
import com.backend.repository.BlogRepository;
import com.backend.repository.CategoryBlogRepository;
import com.backend.repository.DeliveryRepository;
import com.backend.repository.product.ProductRepository;
import com.backend.repository.rental.RentalDetailRepository;
import com.backend.repository.rental.RentalRepository;
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
public class RentalService {

	RentalMapper rentalMapper;

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

	RentalRepository rentalRepository;

	RentalDetailRepository rentalDetailRepository;

	public String create(RentalCreation requestData, HttpServletRequest request)
			throws ClientProtocolException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();
		User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		Rental rental = new Rental();
		rental.setUser(user);
		rental.setRentalCode(requestData.getCode());
		rental.setDiscountValue(requestData.getDiscountValue());

		if (requestData.getRentalPackage() != null) {
			rental.setRentalPackage(requestData.getRentalPackage());
		}

		Delivery delivery;
		if (requestData.getDelivery() != null) {
			Delivery deliveryCreated = deliveryMapper.toDelivery(requestData.getDelivery());
			deliveryCreated.setUser(user);
			delivery = deliveryRepository.save(deliveryCreated);
		} else {
			delivery = deliveryRepository.findFirstByUserAndIsDefaultTrue(user);
		}
		rental.setDelivery(delivery);

		if (requestData.getPayment().getMethod() == PaymentMethod.COD) {
			rental.setStatus(RentalStatus.PENDING);
		}

		rental.setTotalAmount(requestData.getPayment().getAmount());

		rentalRepository.save(rental);
		List<RentalDetail> rentalDetails = Optional.ofNullable(requestData.getDetailRentals())
				.orElse(Collections.emptyList()).stream().map(detailRequest -> {
					RentalDetail rentalDetail = new RentalDetail();

					Product product = productRepository.findById(detailRequest.getProductId())
							.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
					rentalDetail.setProduct(product);

					Sku sku = skuRepository.findById(detailRequest.getSkuId())
							.orElseThrow(() -> new AppException(ErrorCode.SKU_NOT_FOUND));

					rentalDetail.setSku(sku);
					rentalDetail.setQuantity(detailRequest.getQuantity());
					rentalDetail.setDay(detailRequest.getDay());
					rentalDetail.setHour(detailRequest.getHour());
					rentalDetail.setPrice(detailRequest.getPrice());
					rentalDetail.setRental(rental);
					rentalDetail.setStatus(detailRequest.getStatus());

					return rentalDetail;
				}).collect(Collectors.toList());

		rentalDetailRepository.saveAll(rentalDetails);
		rental.setRentalDetails(rentalDetails);
		String app_trans_id = Helpers.getCurrentTimeString("yyMMdd") + "_" + Integer.parseInt(Helpers.handleRandom(7));
		RentalStatus rentalStatus = requestData.getPayment().getMethod() != PaymentMethod.COD ? RentalStatus.UNPAID
				: RentalStatus.PENDING;

		rental.setStatus(rentalStatus);

		rentalRepository.save(rental);
		PaymentRental(requestData.getPayment(), rental, app_trans_id);

		if (requestData.getPayment().getMethod() == PaymentMethod.ZaloPay) {
			String url = paymentService.createPaymentZaloUrl(requestData.getPayment().getAmount(), app_trans_id,
					"https://f66a-113-166-213-84.ngrok-free.app/api/payment/rental/zalo/callback",
					"http://localhost:3000/checkout/rental/payment/success");
			if (url != null)
				return url;
		}

		if (requestData.getPayment().getMethod() == PaymentMethod.VNPay) {
			String url = paymentService.createVnPayUrl((int) requestData.getPayment().getAmount(), request,
					app_trans_id, "http://localhost:8080/api/payment/rental/vn-pay/callback");
			if (url != null)
				return url;
		}

		return app_trans_id;
	}

	private void PaymentRental(PaymentRequest paymentRequest, Rental rental, String app_trans_id) {
		Payment payment = Payment.builder().amount(paymentRequest.getAmount()).method(paymentRequest.getMethod())
				.status(paymentRequest.getStatus()).rental(rental).user(rental.getUser()).appTransId(app_trans_id)
				.build();
		paymentRepository.save(payment);
	}

	public PagedResponse<RentalResponse> getAll(Map<String, String> params) {
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

		Specification<Rental> spec = Specification.where(null);

		if (params.containsKey("status")) {
			String status = params.get("status");
			spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status));
		}

		if ("ROLE_USER".equals(roleUser)) {
			spec = spec
					.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("id"), idUser));
		}

		Page<Rental> rentalPage = rentalRepository.findAll(spec, pageable);
		List<RentalResponse> rentalResponses = rentalPage.getContent().stream().map(rentalMapper::toRentalResponse)
				.collect(Collectors.toList());

		return new PagedResponse<>(rentalResponses, page + 1, rentalPage.getTotalPages(), rentalPage.getTotalElements(),
				limit);
	}
}
