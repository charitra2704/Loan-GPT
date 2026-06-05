package com.ram.loangpt.service.processor;

import com.ram.loangpt.dto.*;
import com.ram.loangpt.enums.ScenarioType;
import com.ram.loangpt.service.ScheduleService;
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
class LumpsumPaymentScenarioProcessorTest {
    @Autowired
    private ScheduleService scheduleService;

    LoanRequest loanRequest=new LoanRequest();
    LoanParameters loanParameters=new LoanParameters();
    List<Scenario> scenarios=new ArrayList<>();
    LumpSumPaymentScenario lumpSumPaymentScenario=new LumpSumPaymentScenario();
    Schedule schedule=new Schedule();

    void setInput(){
        loanParameters.setTenureInMonths(240);
        loanParameters.setPrincipal(BigDecimal.valueOf(5000000));
        loanParameters.setInterestRate(BigDecimal.valueOf(8));

        lumpSumPaymentScenario.setStartMonth(36);
        lumpSumPaymentScenario.setAmount(BigDecimal.valueOf(300000));
        lumpSumPaymentScenario.setScenarioType(ScenarioType.LUMP_SUM_PREPAYMENT);
        scenarios.add(lumpSumPaymentScenario);

        loanRequest.setLoanParameters(loanParameters);
        loanRequest.setScenarios(scenarios);

        schedule=scheduleService.generateSchedule(loanRequest);
    }

    @Test
    void shouldIncreaseExtraPaymentIfMonthIsStartMonth(){

        setInput();

        Schedule schedule=scheduleService.generateSchedule(loanRequest);
        List<ScheduleEntry> scheduleEntries=schedule.getSchedule();


        assertTrue(scheduleEntries.get(lumpSumPaymentScenario.getStartMonth()-1).getExtraPayment().compareTo(
                lumpSumPaymentScenario.getAmount())>=0);
    }

    @Test
    void shouldOutstandingPrincipalBeZeroForLastInstallment(){

        setInput();

        ScheduleEntry last=schedule.getSchedule().getLast();

        assertEquals(BigDecimal.ZERO,last.getOutstandingPrincipal());
    }
}