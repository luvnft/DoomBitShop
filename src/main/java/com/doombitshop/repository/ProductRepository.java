package com.doombitshop.repository;

import com.doombitshop.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@EnableReactiveMongoRepositories
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
    Mono<Boolean> existsByName(String productName);
    Mono<Product> findByName(String productName);
}
