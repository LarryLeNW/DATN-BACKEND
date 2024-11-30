package com.backend.service;

import com.backend.dto.response.Statistics.StatisticsResponse;
import com.backend.entity.Order;
import com.backend.repository.order.OrderRepository;

import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Service
public class StatisticsService {

	@Autowired
	OrderRepository orderRepository;

	// Phương thức tính thống kê theo tháng
	public StatisticsResponse calculateStatistics(String month) {
		// Xác định ngày bắt đầu và kết thúc của tháng
		LocalDateTime startOfMonth = LocalDateTime.of(LocalDateTime.now().getYear(), Month.valueOf(month.toUpperCase()),
				1, 0, 0);
		LocalDateTime endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.toLocalDate().lengthOfMonth()).withHour(23)
				.withMinute(59).withSecond(59);

		// Lấy danh sách các đơn hàng trong tháng
		List<Order> orders = orderRepository.findByCreatedAtBetween(startOfMonth, endOfMonth);

		// Tính toán các thống kê
		double revenue = 0.0;
		int customers = 0;
		int ordersCount = orders.size();

		for (Order order : orders) {
			revenue += order.getTotal_amount(); // Tính doanh thu từ tổng tiền đơn hàng
			if (order.getUser() != null) {
				customers++; // Giả sử mỗi đơn hàng có một khách hàng duy nhất
			}
		}

		return null;
	}
}
