package com.example.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private paymentMode mode;
    private long amount;
    private long orderId;
    public enum paymentMode {
        CASH,
        PAYPAL,
        DEBIT_CARD,
        CREDIT_CARD,
        APPLE_PAY
    }
}
