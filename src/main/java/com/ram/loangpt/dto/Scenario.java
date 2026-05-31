package com.ram.loangpt.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ram.loangpt.enums.ScenarioType;

/**
 * @author chari
 **/
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "scenarioType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LumpSumPaymentScenario.class, name = "LUMP_SUM_PREPAYMENT"),
        @JsonSubTypes.Type(value = RecurringPaymentScenario.class, name = "RECURRING_PREPAYMENT"),
        @JsonSubTypes.Type(value = InterestRateChangeScenario.class, name = "INTEREST_RATE_CHANGE"),
        @JsonSubTypes.Type(value = RecurringEmiChangeScenario.class, name = "RECURRING_EMI_INCREASE")
})
public abstract class Scenario {
    private ScenarioType scenarioType;
    private Integer startMonth;

    public ScenarioType getScenarioType() {
        return scenarioType;
    }

    public void setScenarioType(ScenarioType scenarioType) {
        this.scenarioType = scenarioType;
    }

    public Integer getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(Integer startMonth) {
        this.startMonth = startMonth;
    }

}
