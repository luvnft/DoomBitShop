package com.doombitshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequest {
        private String name;
        private String description;
        private String pricing_type;
        private LocalPrice local_price; // Corrected field name

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class LocalPrice {
                private String amount;
                private String currency;
        }
}
