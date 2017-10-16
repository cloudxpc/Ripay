package com.rickxpc.ripay.repository;

import com.rickxpc.ripay.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Payment findPaymentByTradeNo(String tradeNo);

    List<Payment> findPaymentsByCreationDateBetween(Date from, Date to);
}
