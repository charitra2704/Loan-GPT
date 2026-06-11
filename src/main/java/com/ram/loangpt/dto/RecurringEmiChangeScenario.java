package com.ram.loangpt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ram.loangpt.enums.FrequencyType;

import java.math.BigDecimal;

/**
 * @author chari
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecurringEmiChangeScenario extends Scenario {

    private BigDecimal emiChangedRate;
    private FrequencyType frequencyType;


    public BigDecimal getEmiChangedRate() {
        return emiChangedRate;
    }

    public void setEmiChangedRate(BigDecimal percentage) {
        this.emiChangedRate = percentage;
    }

    public FrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }
}
