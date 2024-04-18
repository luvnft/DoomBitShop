package com.doombitshop.repository;

import com.doombitshop.model.Order;
import com.doombitshop.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableReactiveMongoRepositories
public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
}
