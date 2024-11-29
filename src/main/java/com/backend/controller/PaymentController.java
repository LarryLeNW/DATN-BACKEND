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
import org.springframework.web.bind.annotation.RestController;

import com.backend.constant.Type.OrderStatusType;
import com.backend.constant.Type.PaymentStatus;
import com.backend.dto.response.ApiResponse;
import com.backend.dto.response.order.OrderResponse;
import com.backend.entity.Order;
import com.backend.entity.Payment;
import com.backend.entity.User;
import com.backend.exception.AppException;
import com.backend.exception.ErrorCode;
import com.backend.repository.order.OrderRepository;
import com.backend.repository.order.PaymentRepository;
import com.backend.repository.user.UserRepository;

import jakarta.xml.bind.DatatypeConverter;
import lombok.extern.slf4j.Slf4j;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/payment")
@Slf4j
public class PaymentController {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private String key2 = "kLtgPl8HHhfvMuDHPwKfgfsY4Ydm9eIz";
	private Mac HmacSHA256;

	@Autowired
	OrderRepository orderRepository;

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
				
				
				
				 String redirectUrl = "http://localhost:3000"  + "/payment?apptransid=" + data.getString("app_trans_id");
		         
			}
		} catch (Exception ex) {
			result.put("return_code", 0);
			result.put("return_message", ex.getMessage());
		}

		log.info(result.toString());
	

		return result.toString();
	}

	@GetMapping("/check/{transId}")
	public ApiResponse<Payment> check(@PathVariable String transId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String roleUser = auth.getAuthorities().iterator().next().toString();
		String idUser = auth.getName();

		User user = userRepository.findById(idUser).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
		Payment paymentFound = paymentRepository.findByAppTransIdAndUser(transId , user);
		
		if(paymentFound == null) throw new RuntimeException("Not found your payment...");
		
		return ApiResponse.<Payment>builder().result(paymentFound).build();
	}

}
