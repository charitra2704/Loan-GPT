package com.ram.loangpt.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;

/**
 * @author chari
 **/
public class LumpSumPaymentScenario extends Scenario {

    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
