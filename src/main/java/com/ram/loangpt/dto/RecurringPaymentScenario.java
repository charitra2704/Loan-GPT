package com.ram.loangpt.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ram.loangpt.enums.FrequencyType;

import java.math.BigDecimal;

/**
 * @author chari
 **/

public class RecurringPaymentScenario extends Scenario {

    private BigDecimal amount;
    private FrequencyType frequencyType;


    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public FrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }
}
