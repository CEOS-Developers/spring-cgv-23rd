package com.ceos.spring_cgv_23rd.domain.product.domain;

import com.ceos.spring_cgv_23rd.domain.product.exception.ProductErrorCode;
import com.ceos.spring_cgv_23rd.global.apiPayload.exception.GeneralException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Inventory {


    private Long id;
    private Long theaterId;
    private Long productId;
    private Integer quantity;


    public void decreaseQuantity(int count) {
        if (this.quantity < count) {
            throw new GeneralException(ProductErrorCode.INSUFFICIENT_STOCK);
        }

        this.quantity -= count;
    }

    public void increaseQuantity(int count) {
        this.quantity += count;
    }
}
