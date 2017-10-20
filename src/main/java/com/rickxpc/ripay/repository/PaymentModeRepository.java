package com.rickxpc.ripay.repository;

import com.rickxpc.ripay.domain.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentModeRepository extends JpaRepository<PaymentMode, String> {
}
