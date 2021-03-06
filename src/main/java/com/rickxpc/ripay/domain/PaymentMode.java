package com.rickxpc.ripay.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payment_mode")
public class PaymentMode {
    @Id
    @Column(name = "code")
    private String code;
    @Column(name = "desc")
    private String desc;
    @Column(name = "payment_brand_code")
    private String paymentBrandCode;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPaymentBrandCode() {
        return paymentBrandCode;
    }

    public void setPaymentBrandCode(String paymentBrandCode) {
        this.paymentBrandCode = paymentBrandCode;
    }
}
