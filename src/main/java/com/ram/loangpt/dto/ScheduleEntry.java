package com.ram.loangpt.dto;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

/**
 * @author chari
 **/
public class ScheduleEntry {
    private Integer installmentNumber;
    private BigDecimal installmentAmount;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal outstandingPrincipal;
    private BigDecimal extraPayment;

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    public void setOutstandingPrincipal(BigDecimal outstandingPrincipal) {
        this.outstandingPrincipal = outstandingPrincipal;
    }

    public BigDecimal getExtraPayment() {
        return extraPayment;
    }

    public void setExtraPayment(BigDecimal extraPayment) {
        this.extraPayment = extraPayment;
    }
}
