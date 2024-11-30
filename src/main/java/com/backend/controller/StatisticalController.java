package com.backend.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.response.Statistics.StatisticsResponse;
import com.backend.service.OrderService;
import com.backend.service.StatisticsService;

@RestController
@RequestMapping("/api/statistics")
public class StatisticalController {
	@Autowired
    private StatisticsService statisticsService;

    @GetMapping("/api/statistics/{month}")
    public StatisticsResponse getStatistics(@PathVariable String month) {
        return statisticsService.calculateStatistics(month);
    }
}

