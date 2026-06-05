package com.ram.loangpt.service.processor;

import com.ram.loangpt.dto.LoanParameters;
import com.ram.loangpt.dto.Scenario;
import com.ram.loangpt.dto.ScheduleEntry;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author chari
 **/
@Component
public interface ScenarioProcessor {
    List<ScheduleEntry> processScenario(Scenario scenario, List<ScheduleEntry> schedule, LoanParameters loanParameters);
}
