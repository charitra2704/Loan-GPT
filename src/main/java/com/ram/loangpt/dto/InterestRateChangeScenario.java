package com.ram.loangpt.dto;

import java.math.BigDecimal;

/**
 * @author chari
 **/
public class InterestRateChangeScenario extends Scenario {

    private BigDecimal changedRate;

    public BigDecimal getChangedRate() {
        return changedRate;
    }

    public void setAmount(BigDecimal changedRate) {
        this.changedRate = changedRate;
    }
}
