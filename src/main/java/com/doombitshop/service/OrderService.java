package com.doombitshop.service;

import com.doombitshop.dto.OrderRequest;
import com.doombitshop.exception.GlobalExceptionHandler;
import com.doombitshop.model.Order;
import com.doombitshop.model.TransactionStatus;
import com.doombitshop.repository.OrderRepository;
import com.doombitshop.repository.ProductRepository;
import com.doombitshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final CoinbaseService coinbaseService;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, CoinbaseService coinbaseService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.coinbaseService = coinbaseService;
    }


    public Mono<Order> createOrder(OrderRequest orderRequest) {
        return userRepository.findById(orderRequest.getUserId())
                .switchIfEmpty(Mono.error(new GlobalExceptionHandler.UserNotFoundException(orderRequest.getUserId())))
                .flatMap(user -> productRepository.findById(orderRequest.getProductId())
                        .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                        .flatMap(product -> {
                            if (orderRequest.getQuantity() > product.getStock()) {
                                return Mono.error(new GlobalExceptionHandler.InsufficientStockException(product.getName()));
                            }

                            Order order = new Order();
                            order.setCustomerId(orderRequest.getUserId());
                            order.setProduct(product);
                            order.setTimeStamp(LocalDateTime.now());
                            order.setQuantity(orderRequest.getQuantity());
                            order.setTotalAmount(orderRequest.getQuantity() * product.getPrice());
                            order.setStatus(TransactionStatus.PENDING);
                            return orderRepository.save(order);
                        })
                        .flatMap(order -> coinbaseService.createCharge(order.getProduct().getId())
                                .publishOn(Schedulers.boundedElastic())
                                .mapNotNull(chargeId -> {
                                    order.setCoinbaseChargeId(chargeId);
                                    order.setStatus(TransactionStatus.PENDING);  // Assume pending until confirmation
                                    return orderRepository.save(order).block();  // Save the updated order
                                })));
    }

}
