package com.doombitshop.service;

import com.doombitshop.dto.ChargeRequest;
import com.doombitshop.dto.CoinbaseChargeResponse;
import com.doombitshop.model.Product;
import com.doombitshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class CoinbaseService {

    @Value("${coinbase.api.key}")
    private String apiKey;

    private final ProductRepository productRepository;
    private final WebClient webClient;

    @Autowired
    public CoinbaseService(ProductRepository productRepository, WebClient.Builder webClientBuilder) {
        this.productRepository = productRepository;
        this.webClient = webClientBuilder.baseUrl("https://api.commerce.coinbase.com").build();
    }

    public Mono<String> createCharge(String productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")))
                .flatMap(this::buildChargeRequestDTO)
                .flatMap(this::sendChargeRequestToCoinbase);
    }

    private Mono<ChargeRequest> buildChargeRequestDTO(Product product) {
        if (product.getName() == null || product.getDescription() == null || product.getPrice() == 0.0) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product details are incomplete or invalid."));
        }

        ChargeRequest.LocalPrice localPrice = new ChargeRequest.LocalPrice(
                String.format("%.2f", product.getPrice()), "USD"
        );

        ChargeRequest chargeRequest = new ChargeRequest();
        chargeRequest.setName(product.getName());
        chargeRequest.setDescription(product.getDescription());
        chargeRequest.setPricing_type("fixed_price");
        chargeRequest.setLocal_price(localPrice);

        return Mono.just(chargeRequest);
    }

    private Mono<String> sendChargeRequestToCoinbase(ChargeRequest chargeRequest) {
        return webClient.post()
                .uri("/charges")
                .header("X-CC-Api-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(chargeRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class).flatMap(errorDetails -> {
                            System.out.println("Error details from Coinbase: " + errorDetails);
                            return Mono.error(new ResponseStatusException(response.statusCode(), "Failed to send charge request to Coinbase: " + errorDetails));
                        })
                )
                .bodyToMono(CoinbaseChargeResponse.class)
                .flatMap(response -> {
                    if (response.getData() != null && response.getData().getHosted_url() != null) {
                        return Mono.just(response.getData().getHosted_url());
                    } else {
                        return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve payment URL"));
                    }
                });
    }
}
