package com.backend.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.backend.constant.Type.DiscountVoucherType;
import com.backend.constant.Type.VoucherType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Statistics {
	private String month;
    private double revenue;
    private int customers;
    private int orders;
}
