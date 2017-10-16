package com.rickxpc.ripay.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "payment", schema = "wechat")
public class Payment {
    @Id
    @Column(name = "Id")
    private String id;
    @Column(name = "UserId")
    private String userId;
    @Column(name = "PaymentTypeCode")
    private String paymentTypeCode;
    @Column(name = "TradeNo")
    private String tradeNo;
    @Column(name = "Status")
    private Character status;
    @Column(name = "Title")
    private String title;
    @Column(name = "Attach")
    private String attach;
    @Column(name = "TotalFee")
    private BigDecimal totalFee;
    @Column(name = "RemoteIP")
    private String remoteIP;
    @Column(name = "TradeType")
    private String tradeType;
    @Column(name = "DeviceInfo")
    private String deviceInfo;
    @Column(name = "TransactionId")
    private String transactionId;
    @Column(name = "IsSubscribe")
    private Character isSubscribe;
    @Column(name = "BankType")
    private String bankType;
    @Column(name = "CreationDate")
    private Date creationDate;
    @Column(name = "CompleteDate")
    private Date completeDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPaymentTypeCode() {
        return paymentTypeCode;
    }

    public void setPaymentTypeCode(String paymentTypeCode) {
        this.paymentTypeCode = paymentTypeCode;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
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

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Character getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(Character isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
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