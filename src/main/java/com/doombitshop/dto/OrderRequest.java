package com.doombitshop.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String userId;
    private String productId;
    private int quantity;
}
