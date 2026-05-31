package com.ram.loangpt.dto;

import com.ram.loangpt.enums.FrequencyType;

/**
 * @author chari
 **/
public class RecurringEmiChangeScenario extends Scenario {

    private Float percentage;
    private FrequencyType frequencyType;


    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public FrequencyType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(FrequencyType frequencyType) {
        this.frequencyType = frequencyType;
    }
}
