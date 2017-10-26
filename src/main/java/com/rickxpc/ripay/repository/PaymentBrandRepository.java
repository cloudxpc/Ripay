package com.rickxpc.ripay.repository;

import com.rickxpc.ripay.domain.PaymentBrand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentBrandRepository extends JpaRepository<PaymentBrand, String> {
}
