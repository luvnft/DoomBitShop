package com.doombitshop.controller;

import com.doombitshop.dto.ProductDTO;
import com.doombitshop.exception.GlobalExceptionHandler;
import com.doombitshop.model.Product;
import com.doombitshop.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping("/add")
    public Mono<ResponseEntity<String>> addProduct(@RequestBody ProductDTO productDTO) {
        return productService.addProduct(productDTO)
                .flatMap(response -> Mono.just(ResponseEntity.status(HttpStatus.CREATED).body(response)))
                .onErrorResume(GlobalExceptionHandler.InvalidProductException.class, error ->
                        Mono.just(ResponseEntity.badRequest().body(error.getMessage())))
                .onErrorResume(GlobalExceptionHandler.ProductAlreadyExistsException.class, error ->
                        Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error.getMessage())))
                .onErrorResume(Exception.class, error ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.getMessage())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/all")
    public Flux<Product> geAllProducts(){
            return productService.getAllProducts();
    }
}
