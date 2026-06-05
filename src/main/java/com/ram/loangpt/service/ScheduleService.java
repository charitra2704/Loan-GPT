package com.ram.loangpt.service;

import com.ram.loangpt.dto.*;
import com.ram.loangpt.service.processor.*;
import com.ram.loangpt.utils.FinanceUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chari
 **/
@Service
public class ScheduleService {
    private final ScenarioProcessorFactory scenarioProcessorFactory;

    public ScheduleService(ScenarioProcessorFactory scenarioProcessorFactory) {
        this.scenarioProcessorFactory = scenarioProcessorFactory;
    }

    public Schedule generateSchedule(LoanRequest loanRequest) {
        LoanParameters loanParameters = loanRequest.getLoanParameters();

        //Calculate Installment Amount
        BigDecimal installmentAmount = FinanceUtil.pmt(
                loanParameters.getPrincipal(),
                loanParameters.getInterestRate(),
                loanParameters.getTenureInMonths()
        );

        List<ScheduleEntry> scheduleEntries = generateSchedule(loanParameters, installmentAmount);

        List<Scenario> scenarios = loanRequest.getScenarios();
        if(!CollectionUtils.isEmpty(scenarios)) {
            for (Scenario scenario : scenarios) {
                ScenarioProcessor scenarioProcessor = scenarioProcessorFactory
                        .getScenarioProcessor(scenario.getScenarioType());
                scheduleEntries = scenarioProcessor
                        .processScenario(scenario, scheduleEntries, loanParameters);
            }
        }else{
            ScheduleEntry last=scheduleEntries.getLast();
            last.setOutstandingPrincipal(BigDecimal.ZERO);
        }
        return prepareResponse(installmentAmount, scheduleEntries);
    }

    private static Schedule prepareResponse(BigDecimal installmentAmount, List<ScheduleEntry> scheduleEntries) {
        Schedule schedule=new Schedule();
        schedule.setInstallmentAmount(installmentAmount);

        //Calculate total Interest Paid
        BigDecimal totalInterestPayable= scheduleEntries.stream()
                .map(ScheduleEntry::getInterest)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        schedule.setTotalInterestPayable(totalInterestPayable);

        //Calculate total Principal paid
        BigDecimal totalPrincipalPaid= scheduleEntries.stream()
                        .map(sc -> sc.getPrincipal().add(sc.getExtraPayment()))
                        .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal totalPayment=totalPrincipalPaid.add(totalInterestPayable);
        schedule.setTotalPayment(totalPayment);


        schedule.setSchedule(scheduleEntries);
        return schedule;
    }

    private static List<ScheduleEntry> generateSchedule(LoanParameters loanParameters, BigDecimal installmentAmount) {
        int numberOfMonths = loanParameters.getTenureInMonths();

        //Create a Schedule Entry List
        List<ScheduleEntry> scheduleEntries=new ArrayList<>();

        BigDecimal currentOutstandingPrincipal= loanParameters.getPrincipal();
        for(int month=1;month<=numberOfMonths;month++){

            ScheduleEntry scheduleEntry=new ScheduleEntry();

            scheduleEntry.setInstallmentNumber(month);
            scheduleEntry.setInstallmentAmount(installmentAmount);

            BigDecimal interestPaid=FinanceUtil.calculateInterest(currentOutstandingPrincipal, loanParameters.getInterestRate());
            scheduleEntry.setInterest(interestPaid);

            BigDecimal principalPaid= installmentAmount.subtract(interestPaid);
            scheduleEntry.setPrincipal(principalPaid);

            currentOutstandingPrincipal = currentOutstandingPrincipal.subtract(principalPaid);
            scheduleEntry.setOutstandingPrincipal(currentOutstandingPrincipal);
            scheduleEntry.setExtraPayment(BigDecimal.ZERO);
            scheduleEntries.add(scheduleEntry);
        }

        return scheduleEntries;
    }
}
