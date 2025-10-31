package com.fourweekdays.fourweekdays.product.model.dto.request;

import com.fourweekdays.fourweekdays.product.model.entity.ProductStatus;
import lombok.Getter;

@Getter
public class ProductStatusUpdateDto {
    private ProductStatus status;
    private String changedBy;
}
