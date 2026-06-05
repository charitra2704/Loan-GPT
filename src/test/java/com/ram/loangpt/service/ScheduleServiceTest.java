package com.ram.loangpt.service;

import com.ram.loangpt.dto.*;
import com.ram.loangpt.utils.FinanceUtil;
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
    private  ScheduleService scheduleService;

    @Test
    void shouldCalculateCorrectEMI() {

        //Test if PMT Formula works fine
        BigDecimal principal=BigDecimal.valueOf(5000000);
        BigDecimal annualInterestRate=BigDecimal.valueOf(8);
        int numberOfMonths=240;
        BigDecimal emi= FinanceUtil.pmt(principal,annualInterestRate,numberOfMonths);
        assertEquals(
                0,
                emi.setScale(2)
                        .compareTo(BigDecimal.valueOf(41822.00))
        );
    }

    @Test
    void shouldIncreaseEMIWhenPrincipalIncreases(){

        //Test if emi increases as principal increases with interest rate and tenure months same
        BigDecimal emi1=FinanceUtil.pmt(BigDecimal.valueOf(5000000),BigDecimal.valueOf(8),240);
        BigDecimal emi2=FinanceUtil.pmt(BigDecimal.valueOf(6000000),BigDecimal.valueOf(8),240);
        assertTrue(emi2.compareTo(emi1)>0);
    }

    @Test
    void shouldIncreaseEMIWhenInterestRateIncreases(){

        //Test if emi increases as interest rate increases with principal and tenure months same
        BigDecimal emi1=FinanceUtil.pmt(BigDecimal.valueOf(5000000),BigDecimal.valueOf(8),240);
        BigDecimal emi2=FinanceUtil.pmt(BigDecimal.valueOf(5000000),BigDecimal.valueOf(9),240);
        assertTrue(emi2.compareTo(emi1)>0);
    }

    @Test
    void shouldDecreaseEMIWhenTenureIncreases(){

        //Test if emi decreases as tenure months increases with principal and interest rate same
        BigDecimal emi1=FinanceUtil.pmt(BigDecimal.valueOf(5000000),BigDecimal.valueOf(8),240);
        BigDecimal emi2=FinanceUtil.pmt(BigDecimal.valueOf(5000000),BigDecimal.valueOf(8),360);
        assertTrue(emi1.compareTo(emi2)>0);
    }

    @Test
    void shouldCalculateCorrectInterest(){

        //Test if interest calculation works fine
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
    void shouldIncreaseInterestWhenInterestRateIncreases(){

        //Test if Interest increases when interest rate increases
        BigDecimal interest1=FinanceUtil.calculateInterest(BigDecimal.valueOf(5000000),BigDecimal.valueOf(8));
        BigDecimal interest2=FinanceUtil.calculateInterest(BigDecimal.valueOf(5000000),BigDecimal.valueOf(9));
        assertTrue(interest2.compareTo(interest1)>0);
    }

    @Test
    void shouldIncreaseInterestWhenCurrentOutstandingPrincipalIncreases(){

        //Test if Interest increases when current outstanding principal increases
        BigDecimal interest1=FinanceUtil.calculateInterest(BigDecimal.valueOf(5000000),BigDecimal.valueOf(8));
        BigDecimal interest2=FinanceUtil.calculateInterest(BigDecimal.valueOf(6000000),BigDecimal.valueOf(8));
        assertTrue(interest2.compareTo(interest1)>0);
    }

    @Test
    void shouldEqualScheduleSizeAsTenureInMonthsIfNoScenariosProvided(){

        LoanRequest loanRequest=new LoanRequest();
        LoanParameters loanParameters=new LoanParameters();
        loanParameters.setTenureInMonths(240);
        loanParameters.setPrincipal(BigDecimal.valueOf(5000000));
        loanParameters.setInterestRate(BigDecimal.valueOf(8));

        loanRequest.setLoanParameters(loanParameters);

        Schedule schedule=scheduleService.generateSchedule(loanRequest);
        List<ScheduleEntry> scheduleEntries=schedule.getSchedule();

        assertEquals(240,
                scheduleEntries.size());
    }
}