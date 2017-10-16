package com.rickxpc.ripay.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "paymenttype", schema = "wechat")
public class PaymentType {
	@Id
	@Column(name="PaymentTypeCode")
	private String paymentTypeCode;
	@Column(name="PaymentTypeDsc")
	private String paymentTypeDsc;

	public String getPaymentTypeCode() {
		return paymentTypeCode;
	}

	public void setPaymentTypeCode(String paymentTypeCode) {
		this.paymentTypeCode = paymentTypeCode;
	}

	public String getPaymentTypeDsc() {
		return paymentTypeDsc;
	}

	public void setPaymentTypeDsc(String paymentTypeDsc) {
		this.paymentTypeDsc = paymentTypeDsc;
	}
}