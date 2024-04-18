package com.doombitshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
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
