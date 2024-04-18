package com.doombitshop.service;

import com.doombitshop.dto.ProductDTO;
import com.doombitshop.exception.GlobalExceptionHandler;
import com.doombitshop.model.Product;
import com.doombitshop.repository.ProductRepository;
import com.doombitshop.utils.UserMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    private final UserMapper userMapper;

    private final ProductRepository productRepository;

    public ProductService(UserMapper userMapper, ProductRepository productRepository) {
        this.userMapper = userMapper;
        this.productRepository = productRepository;
    }

    public Mono<String> addProduct(ProductDTO productDTO) {
        return Mono.defer(() -> {
            if (productDTO.getName() == null || productDTO.getName().isEmpty()) {
                return Mono.error(new GlobalExceptionHandler.InvalidProductException("Product name cannot be null or empty"));
            }
            System.out.println(productDTO);

            return productAlreadyExists(productDTO.getName()).flatMap(productExists -> {
                if (productExists) {
                    return Mono.error(new GlobalExceptionHandler.ProductAlreadyExistsException(productDTO.getName()));
                } else {
                    Product product = userMapper.productDtoToProduct(productDTO);
                    return productRepository.save(product).then(Mono.just("Product " + productDTO.getName() + " was added"));
                }
            });
        }).onErrorResume(GlobalExceptionHandler.InvalidProductException.class, Mono::error).onErrorResume(GlobalExceptionHandler.ProductAlreadyExistsException.class, Mono::error).onErrorResume(Exception.class, e -> Mono.error(new RuntimeException("An unexpected error occurred", e)));
    }

    public Mono<Boolean> productAlreadyExists(String productName) {
        return productRepository.existsByName(productName);
    }

    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Mono<Void> updateAllProductStocks() {
        Map<String, Integer> productStocks = readStocksFromFile();
        return Flux.fromIterable(productStocks.entrySet()).flatMap(entry -> updateProductStock(entry.getKey(), entry.getValue())).then();
    }

    public Mono<Product> updateProductStock(String productName, int newStock) {
        return productRepository.findByName(productName).flatMap(product -> {
            product.setStock(newStock);
            return productRepository.save(product);
        });
    }

    private Map<String, Integer> readStocksFromFile() {
        Map<String, Integer> stockMap = new HashMap<>();
        try {
            File file = new ClassPathResource("NordVpnAccounts.txt").getFile();
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    stockMap.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
            if (lines.isEmpty()){
                throw new GlobalExceptionHandler.InsufficientStockException("insufficient stock");
            };
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stock file", e);
        }
        return stockMap;
    }

}
