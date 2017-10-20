package com.rickxpc.ripay.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(name = "payment_mode_code")
    private String paymentModeCode;
    @Column(name = "payment_type_code")
    private String paymentTypeCode;
    @Column(name = "order_id")
    private String orderId;
    @Column(name = "state")
    private Character state;
    @Column(name = "title")
    private String title;
    @Column(name = "attach")
    private String attach;
    @Column(name = "total_amt")
    private BigDecimal totalAmt;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "creation_date")
    private Date creationDate;
    @Column(name = "complete_date")
    private Date completeDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPaymentModeCode() {
        return paymentModeCode;
    }

    public void setPaymentModeCode(String paymentModeCode) {
        this.paymentModeCode = paymentModeCode;
    }

    public String getPaymentTypeCode() {
        return paymentTypeCode;
    }

    public void setPaymentTypeCode(String paymentTypeCode) {
        this.paymentTypeCode = paymentTypeCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Character getState() {
        return state;
    }

    public void setState(Character state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }
}