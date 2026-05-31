package com.ram.loangpt.service;

import com.ram.loangpt.dto.*;
import com.ram.loangpt.service.processor.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        //Create a new Schedule
        Schedule schedule=new Schedule();


        LoanParameters loanParameters = loanRequest.getLoanParameters();
        //Calculate Installment Amount
        double rate = loanParameters.getInterestRate().doubleValue() / 12 / 100;
        int months = loanParameters.getTenureInMonths();
        double principal=loanParameters.getPrincipal().doubleValue();
        double value =
                (principal * rate * Math.pow(1+rate, months)) /
                        (Math.pow(1 + rate, months) - 1);
        BigDecimal installmentAmount= BigDecimal.valueOf(value)
                .setScale(0, RoundingMode.HALF_UP);
        schedule.setInstallmentAmount(installmentAmount);

        //Calculate total Interest
        double totalInterest=(value*months)-principal;
        BigDecimal totalInterestPayable=BigDecimal.valueOf(totalInterest)
                .setScale(0, RoundingMode.HALF_UP);
        schedule.setTotalInterestPayable(totalInterestPayable);

        //Calculate Total Payment
        double payment=value*months;
        BigDecimal totalPayment=BigDecimal.valueOf(payment).setScale(0,RoundingMode.HALF_UP);
        schedule.setTotalPayment(totalPayment);

        //Create a Schedule Entry
        List<ScheduleEntry> scheduleEntries=new ArrayList<>();

        BigDecimal current_outstandingPrincipal=loanParameters.getPrincipal();
        for(int month=1;month<=months;month++){

            ScheduleEntry scheduleEntry=new ScheduleEntry();

            scheduleEntry.setInstallmentNumber(month);
            scheduleEntry.setInstallmentAmount(installmentAmount);

            double interest=current_outstandingPrincipal.doubleValue()*rate;
            BigDecimal interestPaid=BigDecimal.valueOf(interest).setScale(0,RoundingMode.HALF_UP);
            scheduleEntry.setInterest(interestPaid);

            double principal_entry=value-interest;
            BigDecimal principalPaid=BigDecimal.valueOf(principal_entry).
                    setScale(0,RoundingMode.HALF_UP);
            scheduleEntry.setPrincipal(principalPaid);

            double outstandingPrincipal=current_outstandingPrincipal.doubleValue()-principal_entry;
            BigDecimal outstandingPrincipal_entry=BigDecimal.valueOf(outstandingPrincipal).
                    setScale(0,RoundingMode.HALF_UP);
            scheduleEntry.setOutstandingPrincipal(outstandingPrincipal_entry);
            current_outstandingPrincipal=scheduleEntry.getOutstandingPrincipal();

            scheduleEntries.add(scheduleEntry);
        }

        schedule.setSchedule(scheduleEntries);

        List<Scenario> scenarios=loanRequest.getScenarios();
        if(scenarios!=null) {
            for (Scenario scenario : scenarios) {
                ScenarioProcessor scenarioProcessor = scenarioProcessorFactory.getScenarioProcessor(scenario.getScenarioType());
                schedule=scenarioProcessor.processScenario(scenario, schedule, loanParameters);
            }
        }

        return schedule;
    }
}
