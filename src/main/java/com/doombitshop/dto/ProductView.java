package com.doombitshop.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductView {
    private String productId;
    private String name;
    private double price;
    private String stock;
}
