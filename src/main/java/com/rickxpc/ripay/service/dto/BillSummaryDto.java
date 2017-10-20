package com.rickxpc.ripay.service.dto;

import java.math.BigDecimal;

public class BillSummaryDto {
    private Integer count;
    private BigDecimal totalAmt;
    private BigDecimal totalRefundAmt;
    private BigDecimal totalCouponAmt;
    private BigDecimal totalChargeAmt;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public BigDecimal getTotalRefundAmt() {
        return totalRefundAmt;
    }

    public void setTotalRefundAmt(BigDecimal totalRefundAmt) {
        this.totalRefundAmt = totalRefundAmt;
    }

    public BigDecimal getTotalCouponAmt() {
        return totalCouponAmt;
    }

    public void setTotalCouponAmt(BigDecimal totalCouponAmt) {
        this.totalCouponAmt = totalCouponAmt;
    }

    public BigDecimal getTotalChargeAmt() {
        return totalChargeAmt;
    }

    public void setTotalChargeAmt(BigDecimal totalChargeAmt) {
        this.totalChargeAmt = totalChargeAmt;
    }
}
