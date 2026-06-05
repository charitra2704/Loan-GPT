package com.ram.loangpt.service.processor;

import com.ram.loangpt.dto.*;
import com.ram.loangpt.enums.FrequencyType;
import com.ram.loangpt.enums.ScenarioType;
import com.ram.loangpt.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author chari
 **/
@SpringBootTest
class RecurringEmiScenarioProcessorTest {
    @Autowired
    private ScheduleService scheduleService;

    private final LoanRequest loanRequest=new LoanRequest();
    private final LoanParameters loanParameters=new LoanParameters();
    private final List<Scenario> scenarios=new ArrayList<>();
    private final RecurringEmiChangeScenario recurringEmiChangeScenario=new RecurringEmiChangeScenario();
    private Schedule schedule=new Schedule();

    void setInput(){
        loanParameters.setTenureInMonths(240);
        loanParameters.setPrincipal(BigDecimal.valueOf(5000000));
        loanParameters.setInterestRate(BigDecimal.valueOf(8));

        recurringEmiChangeScenario.setStartMonth(60);
        recurringEmiChangeScenario.setEmiChangedRate(BigDecimal.valueOf(5));
        recurringEmiChangeScenario.setScenarioType(ScenarioType.RECURRING_EMI_CHANGE);
        recurringEmiChangeScenario.setFrequencyType(FrequencyType.ANNUALLY);
        scenarios.add(recurringEmiChangeScenario);
        loanRequest.setLoanParameters(loanParameters);

        loanRequest.setScenarios(scenarios);

        schedule=scheduleService.generateSchedule(loanRequest);
    }

    @Test
    void checkIfEmiIsLessThanInterest(){
        setInput();

        List<ScheduleEntry> scheduleEntries=schedule.getSchedule();

        for(ScheduleEntry scheduleEntry:scheduleEntries){
            assertTrue(scheduleEntry.getInstallmentAmount().compareTo(scheduleEntry.getInterest())>=0);
        }
    }

    @Test
    void shouldOutstandingPrincipalBeZeroForLastInstallment(){

        setInput();

        ScheduleEntry last=schedule.getSchedule().getLast();

        assertEquals(BigDecimal.ZERO,last.getOutstandingPrincipal());
    }

    @Test
    void checkEmiChangeCalculation(){
        setInput();

        List<ScheduleEntry> scheduleEntries=schedule.getSchedule();

        int frequencyFactor = switch (recurringEmiChangeScenario.getFrequencyType()) {
            case MONTHLY -> 1;
            case QUARTERLY -> 3;
            case ANNUALLY -> 12;
            case SEMI_ANNUALLY -> 6;
        };
        BigDecimal currentInstallmentAmount=BigDecimal.ZERO;
        for(ScheduleEntry scheduleEntry:scheduleEntries){
            if(scheduleEntry.getInstallmentNumber()>=recurringEmiChangeScenario.getStartMonth() &&
                    scheduleEntry.getInstallmentNumber()%frequencyFactor==0) {
                BigDecimal actualIncrease=scheduleEntry.getInstallmentAmount().subtract(currentInstallmentAmount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(currentInstallmentAmount,4,RoundingMode.HALF_UP);

                assertEquals(0,actualIncrease.compareTo(recurringEmiChangeScenario.getEmiChangedRate()));
            }

            currentInstallmentAmount=scheduleEntry.getInstallmentAmount();
        }
    }
}