package com.doombitshop.dto;

import com.doombitshop.model.Product;
import com.doombitshop.model.TransactionStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class OrderDTO {
    @Id
    private String orderId;

    private String customerId;

    private Product product;

    private LocalDateTime timeStamp;

    private int quantity;

    private double totalAmount;

    private TransactionStatus status;

    private String coinbaseChargeId;
}
