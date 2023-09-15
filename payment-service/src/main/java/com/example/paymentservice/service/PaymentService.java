package com.example.paymentservice.service;

import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.model.PaymentResponse;

import java.util.List;

public interface PaymentService {
    List<PaymentResponse> getAll();
    PaymentResponse getById(long id);
    PaymentResponse create(PaymentRequest paymentRequest);

}
