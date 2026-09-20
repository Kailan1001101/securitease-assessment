package com.example.store.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CreateOrderRequest {
    private String description;
    private Long customerId;
    private Set<Long> productIds;
}