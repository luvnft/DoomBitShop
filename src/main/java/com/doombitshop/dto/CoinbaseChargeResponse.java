package com.doombitshop.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class CoinbaseChargeResponse {
    private ChargeData data;

@Data
    public static class ChargeData {
        private String hosted_url;

    }
}
