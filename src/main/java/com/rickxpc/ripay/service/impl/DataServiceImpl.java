package com.rickxpc.ripay.service.impl;

import com.rickxpc.ripay.domain.PaymentBrand;
import com.rickxpc.ripay.repository.PaymentBrandRepository;
import com.rickxpc.ripay.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataServiceImpl implements DataService {
    @Autowired
    private PaymentBrandRepository paymentBrandRepository;

    @Override
    public List<PaymentBrand> getAllPaymentBrands() {
        return paymentBrandRepository.findAll();
    }
}
