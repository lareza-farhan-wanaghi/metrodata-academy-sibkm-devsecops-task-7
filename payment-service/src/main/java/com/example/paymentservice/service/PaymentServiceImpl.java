package com.example.paymentservice.service;

import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.exception.CustomException;
import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.model.PaymentResponse;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService{
    private PaymentRepository paymentRepository;

    @Override
    public List<PaymentResponse> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mappingPaymentToPaymentResponses).collect(Collectors.toList());
    }

    @Override
    public PaymentResponse getById(long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                    "Payment with given id " + id + " not found",
                    "PAYMENT_NOT_FOUND",
                    404
                ));
        return mappingPaymentToPaymentResponses(payment);
    }

    @Override
    public PaymentResponse create(PaymentRequest paymentRequest) {
        Payment payment = Payment.builder()
                .mode(paymentRequest.getMode().name())
                .amount(paymentRequest.getAmount())
                .orderId(paymentRequest.getOrderId())
                .status("SUCCESS")
                .date(Instant.now())
                .build();

        Payment res = paymentRepository.save(payment);
        return mappingPaymentToPaymentResponses(res);
    }

    private PaymentResponse mappingPaymentToPaymentResponses(Payment payment) {
        PaymentResponse paymentResponse = new PaymentResponse();
        BeanUtils.copyProperties(payment, paymentResponse);
        return paymentResponse;
    }

    private Payment mappingPaymentRequestToPayment(PaymentRequest paymentRequest) {
        Payment payment = new Payment();
        BeanUtils.copyProperties(paymentRequest, payment);
        return payment;
    }
}
