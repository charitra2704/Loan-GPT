package com.ram.loangpt.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chari
 **/
public class LoanParameters {
    private BigDecimal principal;
    private BigDecimal interestRate;
    private Integer tenureInMonths;

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureInMonths() {
        return tenureInMonths;
    }

    public void setTenureInMonths(Integer tenureInMonths) {
        this.tenureInMonths = tenureInMonths;
    }
}
