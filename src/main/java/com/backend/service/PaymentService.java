package com.backend.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import org.springframework.stereotype.Service;

import com.backend.utils.HMACUtil;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PaymentService {

	public static Map<String, String> getVNPayConfig() {
		Map<String, String> vnpParamsMap = new HashMap<>();
		vnpParamsMap.put("vnp_Version", "2.1.0");
		vnpParamsMap.put("vnp_Command", "pay");
		vnpParamsMap.put("vnp_TmnCode", "58X4B4HP");
		vnpParamsMap.put("vnp_CurrCode", "VND");
		vnpParamsMap.put("vnp_OrderInfo", "nội dung kkk");
		vnpParamsMap.put("vnp_OrderType", "other");
		vnpParamsMap.put("vnp_Locale", "vn");

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnpCreateDate = formatter.format(calendar.getTime());
		vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
		calendar.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(calendar.getTime());
		vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
		return vnpParamsMap;
	}

	public String createVnPayUrl(int d, HttpServletRequest request, String trans_id, String callback_url) {
		Map<String, String> vnpParamsMap = getVNPayConfig();
		long amount = d * 100L;
		vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
		vnpParamsMap.put("vnp_TxnRef", trans_id);
		vnpParamsMap.put("vnp_ReturnUrl", callback_url);
		vnpParamsMap.put("vnp_IpAddr", com.backend.utils.VNPayUtil.getIpAddress(request));
		String queryUrl = com.backend.utils.VNPayUtil.getPaymentURL(vnpParamsMap, true);
		String hashData = com.backend.utils.VNPayUtil.getPaymentURL(vnpParamsMap, false);
		String vnpSecureHash = com.backend.utils.VNPayUtil.hmacSHA512("VRLDWNVWDNPCOEPBZUTWSEDQAGXJCNGZ", hashData);
		queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

		String paymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html" + "?" + queryUrl;
		return paymentUrl;
	}

	private static Map<String, String> stage_zalo_config = new HashMap<String, String>() {
		{
			put("app_id", "2553");
			put("key1", "PcY4iZIKFCIdgZvA6ueMcMHHUbRLYjPL");
			put("key2", "kLtgPl8HHhfvMuDHPwKfgfsY4Ydm9eIz");
			put("endpoint", "https://sb-openapi.zalopay.vn/v2/create");
		}
	};

	public String createPaymentZaloUrl(double totalAmount, String app_trans_id , String callback_url, String redirect_success_url)
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
		embed_data.put("redirecturl", redirect_success_url);

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
				put("callback_url", callback_url);
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

}
