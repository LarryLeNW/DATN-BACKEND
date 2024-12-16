package com.backend.controller;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.constant.Type.OrderStatusType;
import com.backend.constant.Type.PaymentStatus;
import com.backend.constant.Type.RentalStatus;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.entity.Order;
import com.backend.entity.Payment;
import com.backend.entity.User;
import com.backend.entity.rental.Rental;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.repository.order.OrderRepository;
import com.backend.repository.order.PaymentRepository;
import com.backend.repository.rental.RentalRepository;
import com.backend.repository.user.UserRepository;
import com.backend.service.PaymentService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/payment")
@Slf4j
public class PaymentController {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private String key2 = "kLtgPl8HHhfvMuDHPwKfgfsY4Ydm9eIz";
	private Mac HmacSHA256;

	@Autowired
	PaymentService paymentService;
	
	@Autowired
	OrderRepository orderRepository;

	@Autowired
	RentalRepository rentalRepository; 
	
	@Autowired
	PaymentRepository paymentRepository;

	@Autowired
	UserRepository userRepository;

	public PaymentController() throws Exception {
		HmacSHA256 = Mac.getInstance("HmacSHA256");
		HmacSHA256.init(new SecretKeySpec(key2.getBytes(), "HmacSHA256"));
	}

	@PostMapping("/zalo/callback")
	public String callback(@RequestBody String jsonStr) {
		JSONObject result = new JSONObject();

		try {
			JSONObject cbdata = new JSONObject(jsonStr);
			String dataStr = cbdata.getString("data");
			String reqMac = cbdata.getString("mac");

			byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes());
			String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

			if (!reqMac.equals(mac)) {
				result.put("return_code", -1);
				result.put("return_message", "mac not equal");
			} else {
				JSONObject data = new JSONObject(dataStr);
				Payment paymentFound = paymentRepository.findByAppTransId(data.getString("app_trans_id"));

				if (paymentFound != null) {
					paymentFound.setStatus(PaymentStatus.COMPLETED);
					paymentRepository.save(paymentFound);
					Order orderUpdate = orderRepository.findByPayment(paymentFound);
					orderUpdate.setStatus(OrderStatusType.PENDING);
					orderRepository.save(orderUpdate);
				}

				logger.info("update order's status = success where app_trans_id = " + data.getString("app_trans_id"));

				result.put("return_code", 1);
				result.put("return_message", "success");

			}
		} catch (Exception ex) {
			result.put("return_code", 0);
			result.put("return_message", ex.getMessage());
		}

		log.info(result.toString());

		return result.toString();
	}

	@GetMapping("/vn-pay/callback")
	public void callbackVNPay(@RequestParam Map<String, String> params, HttpServletResponse response)
			throws IOException {
		String txnRef = params.get("vnp_TxnRef");
		String paymentStatus = params.get("vnp_TransactionStatus");

		if ("00".equals(paymentStatus)) {
			Payment paymentFound = paymentRepository.findByAppTransId(txnRef);

			if (paymentFound != null) {
				paymentFound.setStatus(PaymentStatus.COMPLETED);
				paymentRepository.save(paymentFound);
				Order orderUpdate = orderRepository.findByPayment(paymentFound);
				orderUpdate.setStatus(OrderStatusType.PENDING); 
				orderRepository.save(orderUpdate);
				logger.info("Order status updated to success where app_trans_id = " + txnRef);
			}
		} else {
			logger.warning("Payment failed for txnRef: " + txnRef);
		}

		String redirectUrl = "http://localhost:3000/checkout/payment/success?apptransid=" + txnRef;
		response.sendRedirect(redirectUrl);
	}
	
	
	
	
	
	
	@PostMapping("/rental/zalo/callback")
	public String callbackRentalZalo(@RequestBody String jsonStr) {
		JSONObject result = new JSONObject();

		try {
			JSONObject cbdata = new JSONObject(jsonStr);
			String dataStr = cbdata.getString("data");
			String reqMac = cbdata.getString("mac");

			byte[] hashBytes = HmacSHA256.doFinal(dataStr.getBytes());
			String mac = DatatypeConverter.printHexBinary(hashBytes).toLowerCase();

			if (!reqMac.equals(mac)) {
				result.put("return_code", -1);
				result.put("return_message", "mac not equal");
			} else {
				JSONObject data = new JSONObject(dataStr);
				Payment paymentFound = paymentRepository.findByAppTransId(data.getString("app_trans_id"));

				if (paymentFound != null) {
					paymentFound.setStatus(PaymentStatus.COMPLETED);
					paymentRepository.save(paymentFound);
					Rental rentalUpdate = rentalRepository.findByPayment(paymentFound);
					rentalUpdate.setStatus(RentalStatus.PENDING);
					rentalRepository.save(rentalUpdate);
				}

				logger.info("update order's status = success where app_trans_id = " + data.getString("app_trans_id"));

				result.put("return_code", 1);
				result.put("return_message", "success");

			}
		} catch (Exception ex) {
			result.put("return_code", 0);
			result.put("return_message", ex.getMessage());
		}

		log.info(result.toString());

		return result.toString();
	}

	@GetMapping("/rental/vn-pay/callback")
	public void callbackRentalVNPay(@RequestParam Map<String, String> params, HttpServletResponse response)
			throws IOException {
		String txnRef = params.get("vnp_TxnRef");
		String paymentStatus = params.get("vnp_TransactionStatus");

		if ("00".equals(paymentStatus)) {
			Payment paymentFound = paymentRepository.findByAppTransId(txnRef);

			if (paymentFound != null) {
				paymentFound.setStatus(PaymentStatus.COMPLETED);
				paymentRepository.save(paymentFound);
				Rental rentalUpdate = rentalRepository.findByPayment(paymentFound);
				rentalUpdate.setStatus(RentalStatus.PENDING);
				rentalRepository.save(rentalUpdate);
			}
			
		} else {
			logger.warning("Payment failed for txnRef: " + txnRef);
		}

		String redirectUrl = "http://localhost:3000/checkout/rental/payment/success?apptransid=" + txnRef;
		response.sendRedirect(redirectUrl);
	}

	

	@GetMapping("/check/{transId}")
	public ApiResponse<Payment> check(@PathVariable String transId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		Payment paymentFound = paymentRepository.findByAppTransIdAndUser(transId, user);

		if (paymentFound == null)
			throw new RuntimeException("Not found your payment...");

		return ApiResponse.<Payment>builder().result(paymentFound).build();
	}

    @GetMapping("/statistics/{month}/{year}")
    public Map<Integer, Map<String, Object>> getPaymentsAndRevenueByDayInMonth(@PathVariable int month, @PathVariable int year) {
        return paymentService.getPaymentsAndRevenueByDayInMonth(month, year);
    }
    
    @GetMapping("/statistics/totals")
    public Map<String, Map<String, Long>> getPaymentTotals() {
        return paymentService.getPaymentTotals();
    }
}
