package com.rickxpc.ripay.repository;

import com.rickxpc.ripay.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

}
