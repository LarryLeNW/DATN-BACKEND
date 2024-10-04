package com.backend.dto.test;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkuDTO {
    private Long id;
    private String code;
    private Long price;
    private String attributes;
}
