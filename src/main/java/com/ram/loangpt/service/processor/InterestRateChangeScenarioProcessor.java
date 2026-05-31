package com.ram.loangpt.service.processor;

import com.ram.loangpt.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chari
 **/
@Service
public class InterestRateChangeScenarioProcessor implements ScenarioProcessor {
    public Schedule processScenario(Scenario scenario, Schedule schedule, LoanParameters loanParameters){
        if(scenario instanceof InterestRateChangeScenario interestRateChangeScnario) {

            int startMonth=scenario.getStartMonth();
            int month=1;

            //Calculate Installment Amount
            double rate = loanParameters.getInterestRate().doubleValue() / 12 / 100;
            int months = loanParameters.getTenureInMonths();
            double principal=loanParameters.getPrincipal().doubleValue();
            double value =
                    (principal * rate * Math.pow(1+rate, months)) /
                            (Math.pow(1 + rate, months) - 1);
            BigDecimal installmentAmount= BigDecimal.valueOf(value)
                    .setScale(0, RoundingMode.HALF_EVEN);
            schedule.setInstallmentAmount(installmentAmount);

            //Calculate total Interest
            double totalInterest=(value*months)-principal;
            BigDecimal totalInterestPayable=BigDecimal.valueOf(totalInterest)
                    .setScale(0, RoundingMode.HALF_EVEN);
            schedule.setTotalInterestPayable(totalInterestPayable);

            //Calculate Total Payment
            double payment=value*months;
            BigDecimal totalPayment=BigDecimal.valueOf(payment).setScale(0,RoundingMode.HALF_EVEN);
            schedule.setTotalPayment(totalPayment);

            List<ScheduleEntry> scheduleEntries=new ArrayList<>();

            BigDecimal current_outstandingPrincipal=loanParameters.getPrincipal();

            //Till the month interest rate change is same schedule is as usual
            while(month<startMonth){
                ScheduleEntry scheduleEntry=new ScheduleEntry();
                scheduleEntry.setInstallmentNumber(month);
                scheduleEntry.setInstallmentAmount(installmentAmount);

                double interest=current_outstandingPrincipal.doubleValue()*rate;
                BigDecimal interestPaid=BigDecimal.valueOf(interest).setScale(0, RoundingMode.HALF_EVEN);
                scheduleEntry.setInterest(interestPaid);

                double principal_entry=value-interest;
                BigDecimal principalPaid=BigDecimal.valueOf(principal_entry).
                        setScale(0,RoundingMode.HALF_EVEN);
                scheduleEntry.setPrincipal(principalPaid);

                double outstandingPrincipal=current_outstandingPrincipal.doubleValue()-principal_entry;
                BigDecimal outstandingPrincipal_entry=BigDecimal.valueOf(outstandingPrincipal).
                        setScale(0,RoundingMode.HALF_EVEN);
                scheduleEntry.setOutstandingPrincipal(outstandingPrincipal_entry);
                current_outstandingPrincipal=scheduleEntry.getOutstandingPrincipal();


                scheduleEntries.add(scheduleEntry);

                month++;
            }

            BigDecimal interestRateChangeScenarioChangedRate=interestRateChangeScnario.getChangedRate();
            double changedRate=interestRateChangeScenarioChangedRate.doubleValue()/12/100;

            do{
                ScheduleEntry scheduleEntry=new ScheduleEntry();
                scheduleEntry.setInstallmentNumber(month);
                scheduleEntry.setInstallmentAmount(installmentAmount);

                if(month==startMonth)
                    rate=changedRate;
                double interest=current_outstandingPrincipal.doubleValue()*rate;
                System.out.println(current_outstandingPrincipal+" " +interest);
                BigDecimal interestPaid=BigDecimal.valueOf(interest).setScale(0, RoundingMode.HALF_EVEN);
                scheduleEntry.setInterest(interestPaid);

                double principal_entry=value-interest;
                BigDecimal principalPaid=BigDecimal.valueOf(principal_entry).
                        setScale(0,RoundingMode.HALF_EVEN);
                scheduleEntry.setPrincipal(principalPaid);

                double outstandingPrincipal=current_outstandingPrincipal.doubleValue()-principal_entry;
                BigDecimal outstandingPrincipal_entry=BigDecimal.valueOf(outstandingPrincipal).
                        setScale(0,RoundingMode.HALF_EVEN);
                scheduleEntry.setOutstandingPrincipal(outstandingPrincipal_entry);
                current_outstandingPrincipal=scheduleEntry.getOutstandingPrincipal();


                scheduleEntries.add(scheduleEntry);

                month++;
            }
            while(current_outstandingPrincipal.compareTo(BigDecimal.ZERO)>0);

            schedule.setSchedule(scheduleEntries);
            }
     else {
            throw new IllegalArgumentException("Scenario Mismatch");
        }
        return schedule;
    }
}
