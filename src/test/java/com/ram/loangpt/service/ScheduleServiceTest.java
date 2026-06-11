package com.ram.loangpt.service;

import com.ram.loangpt.dto.*;
import com.ram.loangpt.enums.ScenarioType;
import com.ram.loangpt.utils.FinanceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleServiceTest {


    @Autowired
    private ScheduleService scheduleService;
    private LoanRequest loanRequest;
    private LumpSumPaymentScenario lumpSumPaymentScenario;
    private Schedule schedule;

    @BeforeEach
    void setup(){
        LoanParameters loanParameters = new LoanParameters();
        loanParameters.setTenureInMonths(240);
        loanParameters.setPrincipal(BigDecimal.valueOf(5000000));
        loanParameters.setInterestRate(BigDecimal.valueOf(8));

        loanRequest = new LoanRequest();
        loanRequest.setLoanParameters(loanParameters);

        schedule=scheduleService.generateSchedule(loanRequest);
    }

    @Test
    void shouldCalculateCorrectEMI() {

        LoanParameters loanParameters=loanRequest.getLoanParameters();
        BigDecimal emi= FinanceUtil.pmt(loanParameters.getPrincipal(),loanParameters.getInterestRate(),
                loanParameters.getTenureInMonths());
        assertEquals(
                0,
                emi.setScale(2)
                        .compareTo(BigDecimal.valueOf(41822.00))
        );
    }

    @Test
    void checkWhenInterestRateZero(){
        LoanParameters loanParameters=loanRequest.getLoanParameters();

        loanParameters.setInterestRate(BigDecimal.ZERO);

        Schedule schedule=scheduleService.generateSchedule(loanRequest);

        assertEquals(0,schedule.getInstallmentAmount().compareTo(BigDecimal.valueOf(20833.33)));
    }

    @Test
    void checkWhenInterestRateNegative(){
        LoanParameters loanParameters=loanRequest.getLoanParameters();

        loanParameters.setInterestRate(BigDecimal.valueOf(-8.5));

        assertThrows(IllegalArgumentException.class,()->FinanceUtil.pmt(loanParameters.getPrincipal(),
                loanParameters.getInterestRate(),loanParameters.getTenureInMonths()));
    }

    @Test
    void checkWhenPrincipalZero(){
        LoanParameters loanParameters=loanRequest.getLoanParameters();

        loanParameters.setPrincipal(BigDecimal.ZERO);

        BigDecimal emi=FinanceUtil.pmt(loanParameters.getPrincipal(),loanParameters.getInterestRate()
                ,loanParameters.getTenureInMonths());
        assertEquals(0,emi.compareTo(BigDecimal.ZERO));
    }

    @Test
    void checkWhenPrincipalNegative(){
        LoanParameters loanParameters=loanRequest.getLoanParameters();

        loanParameters.setPrincipal(BigDecimal.valueOf(-5000000));

        assertThrows(IllegalArgumentException.class,()->FinanceUtil.pmt(loanParameters.getPrincipal(),
                loanParameters.getInterestRate(),loanParameters.getTenureInMonths()));
    }

    @Test
    void checkWhenTenureNotWholeNumber(){
        LoanParameters loanParameters=loanRequest.getLoanParameters();

        loanParameters.setTenureInMonths(-240);

        assertThrows(IllegalArgumentException.class,()->FinanceUtil.pmt(loanParameters.getPrincipal(),
                loanParameters.getInterestRate(),loanParameters.getTenureInMonths()));
    }

    @Test
    void shouldCalculateCorrectInterest(){

        BigDecimal currentOutstandingPrincipal = BigDecimal.valueOf(5000000);
        BigDecimal interestRate=BigDecimal.valueOf(8);
        BigDecimal interestPaid=FinanceUtil.calculateInterest(currentOutstandingPrincipal,interestRate);
        assertEquals(
                0,
                interestPaid.setScale(2)
                        .compareTo(BigDecimal.valueOf(33333.33))
        );
    }


    @Test
    void shouldEqualScheduleSizeAsTenureInMonthsIfNoScenariosProvided(){

        Schedule schedule=scheduleService.generateSchedule(loanRequest);
        List<ScheduleEntry> scheduleEntries=schedule.getSchedule();

        assertEquals(240,
                scheduleEntries.size());

        ScheduleEntry last=schedule.getSchedule().getLast();
        assertEquals(BigDecimal.ZERO,last.getOutstandingPrincipal());
    }
}