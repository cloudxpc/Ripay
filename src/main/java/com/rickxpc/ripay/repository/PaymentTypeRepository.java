package com.rickxpc.ripay.repository;

import com.rickxpc.ripay.domain.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, String> {
}
