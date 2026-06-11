package com.ram.loangpt.service.processor;

import com.ram.loangpt.dto.*;
import com.ram.loangpt.enums.FrequencyType;
import com.ram.loangpt.enums.ScenarioType;
import com.ram.loangpt.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author chari
 **/
@SpringBootTest
class RecurringPaymentScenarioProcessorTest {

    @Autowired
    private ScheduleService scheduleService;
    private  LoanRequest loanRequest;
    private  RecurringPaymentScenario recurringPaymentScenario;
    private Schedule schedule;

    @BeforeEach
    void setup(){
        LoanParameters loanParameters=new LoanParameters();
        loanParameters.setTenureInMonths(240);
        loanParameters.setPrincipal(BigDecimal.valueOf(5000000));
        loanParameters.setInterestRate(BigDecimal.valueOf(8));

        recurringPaymentScenario=new RecurringPaymentScenario();
        recurringPaymentScenario.setStartMonth(48);
        recurringPaymentScenario.setAmount(BigDecimal.valueOf(25000));
        recurringPaymentScenario.setScenarioType(ScenarioType.RECURRING_PREPAYMENT);
        recurringPaymentScenario.setFrequencyType(FrequencyType.ANNUALLY);

        List<Scenario> scenarios = new ArrayList<>();
        scenarios.add(recurringPaymentScenario);

        loanRequest=new LoanRequest();
        loanRequest.setLoanParameters(loanParameters);
        loanRequest.setScenarios(scenarios);

        schedule=scheduleService.generateSchedule(loanRequest);
    }

    @Test
    void shouldIncreaseExtraPaymentWhenFrequencyMatches(){

        int startMonth=recurringPaymentScenario.getStartMonth();
        List<ScheduleEntry> scheduleEntries=schedule.getSchedule();

        int frequencyFactor = switch (recurringPaymentScenario.getFrequencyType()) {
            case MONTHLY -> 1;
            case QUARTERLY -> 3;
            case ANNUALLY -> 12;
            case SEMI_ANNUALLY -> 6;
        };

        for(ScheduleEntry scheduleEntry:scheduleEntries){
            if(scheduleEntry.getInstallmentNumber()>=startMonth && scheduleEntry.getInstallmentNumber()%frequencyFactor==0){
                assertTrue(scheduleEntry.getExtraPayment().compareTo(
                        recurringPaymentScenario.getAmount())>=0);
            }
        }

        ScheduleEntry last=schedule.getSchedule().getLast();

        assertEquals(BigDecimal.ZERO,last.getOutstandingPrincipal());
    }

}