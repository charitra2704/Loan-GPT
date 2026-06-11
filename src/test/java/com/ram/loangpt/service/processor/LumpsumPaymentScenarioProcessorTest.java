package com.ram.loangpt.service.processor;

import com.ram.loangpt.dto.*;
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
class LumpsumPaymentScenarioProcessorTest {
    @Autowired
    private ScheduleService scheduleService;
    private LoanRequest loanRequest;
    private LumpSumPaymentScenario lumpSumPaymentScenario;
    private Schedule scheduleWithLumpSum;
    private Schedule scheduleWithoutLumpSum;

    @BeforeEach
    void setup(){
        LoanParameters loanParameters = new LoanParameters();
        loanParameters.setTenureInMonths(240);
        loanParameters.setPrincipal(BigDecimal.valueOf(5000000));
        loanParameters.setInterestRate(BigDecimal.valueOf(8));

        lumpSumPaymentScenario = new LumpSumPaymentScenario();
        lumpSumPaymentScenario.setStartMonth(36);
        lumpSumPaymentScenario.setAmount(BigDecimal.valueOf(300000));
        lumpSumPaymentScenario.setScenarioType(ScenarioType.LUMP_SUM_PREPAYMENT);

        List<Scenario> scenarios = new ArrayList<>();
        scenarios.add(lumpSumPaymentScenario);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanParameters(loanParameters);

        scheduleWithoutLumpSum=scheduleService.generateSchedule(loanRequest);

        loanRequest.setScenarios(scenarios);
        scheduleWithLumpSum =scheduleService.generateSchedule(loanRequest);
    }


    @Test
    void shouldDecreaseOutstandingPrincipalIfMonthIsStartMonth(){
        List<ScheduleEntry> scheduleEntriesWithoutLumpSum=scheduleWithoutLumpSum.getSchedule();
        List<ScheduleEntry> scheduleEntriesWithLumpSum=scheduleWithLumpSum.getSchedule();

        for(ScheduleEntry scheduleEntry: scheduleEntriesWithLumpSum) {
            if(scheduleEntry.getInstallmentNumber().compareTo(lumpSumPaymentScenario.getStartMonth())==0)
                assertTrue(scheduleEntry
                        .getOutstandingPrincipal()
                        .compareTo(scheduleEntriesWithoutLumpSum.get(scheduleEntry.getInstallmentNumber())
                                .getOutstandingPrincipal()) < 0);
        }

        ScheduleEntry last=scheduleEntriesWithLumpSum.getLast();
        assertEquals(BigDecimal.ZERO,last.getOutstandingPrincipal());
    }

    @Test
    void checkIfTenureReduces(){
        List<ScheduleEntry> scheduleEntriesWithoutLumpSum=scheduleWithoutLumpSum.getSchedule();
        List<ScheduleEntry> scheduleEntriesWithLumpSum=scheduleWithLumpSum.getSchedule();

        assertTrue(scheduleEntriesWithLumpSum.size()<scheduleEntriesWithoutLumpSum.size());
    }

}
