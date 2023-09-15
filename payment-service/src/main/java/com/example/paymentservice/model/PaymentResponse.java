package com.example.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private long id;
    private String mode;
    private Instant date;
    private String status;
    private long amount;
    private long orderId;
}
