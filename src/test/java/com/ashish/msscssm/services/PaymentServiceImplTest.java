package com.ashish.msscssm.services;

import com.ashish.msscssm.domain.Payment;
import com.ashish.msscssm.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class PaymentServiceImplTest {
    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;
    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Transactional
    @Test
    public void preAuth(){
        Payment newPayment = paymentService.newPayment(payment);

        paymentService.preAuth(newPayment.getId());

        Payment preAuth = paymentRepository.getReferenceById(newPayment.getId());

        System.out.println(preAuth.getState());

    }
}