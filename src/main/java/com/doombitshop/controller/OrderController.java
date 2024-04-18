package com.doombitshop.controller;

import com.doombitshop.dto.OrderRequest;
import com.doombitshop.model.Order;
import com.doombitshop.service.CoinbaseService;
import com.doombitshop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(path = "/create-charge", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Order> createCharge(@RequestBody @Validated OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }
}
