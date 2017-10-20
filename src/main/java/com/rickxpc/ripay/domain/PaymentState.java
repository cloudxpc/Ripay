package com.rickxpc.ripay.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payment_state")
public class PaymentState {
    @Id
    @Column(name = "code")
    private Character code;
    @Column(name = "desc")
    private String desc;

    public Character getCode() {
        return code;
    }

    public void setCode(Character code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
