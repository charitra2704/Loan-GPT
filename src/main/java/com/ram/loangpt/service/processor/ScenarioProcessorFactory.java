package com.ram.loangpt.service.processor;

import com.ram.loangpt.enums.ScenarioType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author chari
 **/
@Component
public class ScenarioProcessorFactory {

    @Autowired
    private LumpsumPaymentScenarioProcessor lumpsumPaymentScenarioProcessor;

    @Autowired
    private RecurringEmiScenarioProcessor recurringEmiScenarioProcessor;

    @Autowired
    private RecurringPaymentScenarioProcessor  recurringPaymentScenarioProcessor;


    public ScenarioProcessor getScenarioProcessor(ScenarioType scenarioType) {

        switch (scenarioType) {
            case ScenarioType.LUMP_SUM_PREPAYMENT -> {
                return lumpsumPaymentScenarioProcessor;
            }
            case ScenarioType.RECURRING_EMI_CHANGE -> {
                return recurringEmiScenarioProcessor;
            }
            case ScenarioType.RECURRING_PREPAYMENT -> {
                return recurringPaymentScenarioProcessor;
            }
            default -> {
                throw new IllegalArgumentException("Invalid Scenario: " + scenarioType);
            }
        }
    }
}
