package com.rickxpc.ripay.repository;

import com.rickxpc.ripay.domain.Payment;
import com.rickxpc.ripay.domain.PaymentWxPub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface PaymentWxPubRepository extends JpaRepository<PaymentWxPub, UUID> {
    PaymentWxPub findByPaymentOrderId(String orderId);
    List<PaymentWxPub> findByPaymentCreationDateBetween(Date from, Date to);
}
