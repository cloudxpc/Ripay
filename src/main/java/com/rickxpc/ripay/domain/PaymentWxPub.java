package com.rickxpc.ripay.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "payment_wx_pub")
public class PaymentWxPub {
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id")
    private Payment payment;
    @Column(name = "open_id")
    private String openId;
    @Column(name = "remote_ip")
    private String remoteIp;
    @Column(name = "trade_type")
    private String TradeType;
    @Column(name = "device_info")
    private String deviceInfo;
    @Column(name = "bank_type")
    private String BankType;
    @Column(name = "is_subscribe")
    private Character isSubscribe;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getRemoteIp() {
        return remoteIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
    }

    public String getTradeType() {
        return TradeType;
    }

    public void setTradeType(String tradeType) {
        TradeType = tradeType;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getBankType() {
        return BankType;
    }

    public void setBankType(String bankType) {
        BankType = bankType;
    }

    public Character getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(Character isSubscribe) {
        this.isSubscribe = isSubscribe;
    }
}
