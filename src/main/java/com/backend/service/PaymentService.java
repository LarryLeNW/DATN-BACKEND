package com.backend.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PaymentService {

	  public static Map<String, String> getVNPayConfig() {
	        Map<String, String> vnpParamsMap = new HashMap<>();
	        vnpParamsMap.put("vnp_Version", "2.1.0");
	        vnpParamsMap.put("vnp_Command", "pay");
	        vnpParamsMap.put("vnp_TmnCode", "58X4B4HP");
	        vnpParamsMap.put("vnp_CurrCode", "VND");
	        vnpParamsMap.put("vnp_OrderInfo",  "nội dung kkk");
	        vnpParamsMap.put("vnp_OrderType", "other");
	        vnpParamsMap.put("vnp_Locale", "vn");
	        vnpParamsMap.put("vnp_ReturnUrl", "http://localhost:8080/api/payment/vn-pay/callback");
	        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	        String vnpCreateDate = formatter.format(calendar.getTime());
	        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
	        calendar.add(Calendar.MINUTE, 15);
	        String vnp_ExpireDate = formatter.format(calendar.getTime());
	        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
	        return vnpParamsMap;
	    }
	  
	  
	  public String createVnPayUrl(int d , HttpServletRequest request, String trans_id) {
	        Map<String, String> vnpParamsMap = getVNPayConfig();
	        long amount = d * 100L;
	        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
	        vnpParamsMap.put("vnp_TxnRef", trans_id);
	        vnpParamsMap.put("vnp_IpAddr", com.backend.utils.VNPayUtil.getIpAddress(request));
	        String queryUrl = com.backend.utils.VNPayUtil.getPaymentURL(vnpParamsMap, true);
	        String hashData = com.backend.utils.VNPayUtil.getPaymentURL(vnpParamsMap, false);
	        String vnpSecureHash = com.backend.utils.VNPayUtil.hmacSHA512("VRLDWNVWDNPCOEPBZUTWSEDQAGXJCNGZ", hashData);
	        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
	        
	        String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html" + "?" + queryUrl;
	        return paymentUrl;
	    }
}
