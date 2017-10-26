package com.rickxpc.ripay.web.controller;

import com.rickxpc.ripay.domain.PaymentBrand;
import com.rickxpc.ripay.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataController {
    @Autowired
    private DataService dataService;

    @RequestMapping(value = "/getAllPaymentBrands", method = RequestMethod.GET)
    public List<PaymentBrand> getAllPaymentBrands(){
        return dataService.getAllPaymentBrands();
    }
}
