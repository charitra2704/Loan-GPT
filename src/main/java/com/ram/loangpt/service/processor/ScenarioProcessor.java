package com.ram.loangpt.service.processor;

import com.ram.loangpt.dto.LoanParameters;
import com.ram.loangpt.dto.Scenario;
import com.ram.loangpt.dto.Schedule;
import org.springframework.stereotype.Component;

/**
 * @author chari
 **/
@Component
public interface ScenarioProcessor {
    Schedule processScenario(Scenario scenario, Schedule schedule, LoanParameters loanParameters);
}
