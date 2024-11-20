package com.backend.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;

import com.backend.dto.request.brand.BrandCreationRequest;
import com.backend.dto.request.voucher.VoucherCreationRequest;
import com.backend.dto.response.product.ProductResponse;
import com.backend.dto.response.product.ProductResponse.SKUDTO;
import com.backend.dto.response.voucher.VoucherResponse;
import com.backend.entity.Brand;
import com.backend.entity.Product;
import com.backend.entity.Sku;
import com.backend.entity.Voucher;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    Voucher toVoucher(VoucherCreationRequest request);

    VoucherResponse toVoucherResponse(Voucher voucher);

    default List<ProductResponse> mapProducts(Set<Product> products) {
        if (products == null) {
            return null;
        }
        return products.stream()
                .map(this::toDTO)
                .toList();
    }

    default SKUDTO toSkuDTO(Sku sku) {
        if (sku == null) {
            return null;
        }
        ProductResponse.SKUDTO skuDTO = new ProductResponse.SKUDTO();
        skuDTO.setPrice(sku.getPrice());
        skuDTO.setStock(sku.getStock());
        skuDTO.setDiscount(sku.getDiscount());
        skuDTO.setCode(sku.getCode());
        skuDTO.setId(sku.getId());
        skuDTO.setImages(sku.getImages());

        skuDTO.setAttributes(
            sku.getAttributeOptionSkus().stream()
                .collect(Collectors.toMap(
                    attributeOptionSku -> attributeOptionSku.getAttributeOption().getAttribute().getName(),
                    attributeOptionSku -> attributeOptionSku.getAttributeOption().getValue(),
                    (existing, replacement) -> existing, 
                    HashMap::new
                ))
        );

        return skuDTO;
    }
    
	default ProductResponse toDTO(Product product) {
		ProductResponse productDTO = new ProductResponse();
		productDTO.setId(product.getId());
		productDTO.setName(product.getName());
		productDTO.setSlug(product.getSlug());
		productDTO.setSlug(product.getSlug());
		productDTO.setDescription(product.getDescription());
		productDTO.setCreatedAt(product.getCreatedAt());
		productDTO.setUpdatedAt(product.getUpdatedAt());

		List<ProductResponse.SKUDTO> skuDTOs = product.getSkus().stream().map(sku -> {
			ProductResponse.SKUDTO skuDTO = new ProductResponse.SKUDTO();
			skuDTO.setPrice(sku.getPrice());
			skuDTO.setPrice(sku.getPrice());
			skuDTO.setStock(sku.getStock());
			skuDTO.setDiscount(sku.getDiscount());
			skuDTO.setCode(sku.getCode());
			skuDTO.setId(sku.getId());
			skuDTO.setImages(sku.getImages());

			skuDTO.setAttributes(sku.getAttributeOptionSkus().stream()
					.collect(Collectors.toMap(
							attributeOptionSku -> attributeOptionSku.getAttributeOption().getAttribute().getName(),
							attributeOptionSku -> attributeOptionSku.getAttributeOption().getValue(),
							(existing, replacement) -> existing,
							HashMap::new 
			)));

			return skuDTO;
		}).collect(Collectors.toList());

		productDTO.setSkus(skuDTOs);
		return productDTO;
	}
}
