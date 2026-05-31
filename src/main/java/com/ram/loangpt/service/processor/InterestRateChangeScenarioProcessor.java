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
        if(scenario instanceof InterestRateChangeScenario interestRateChangeScenario) {

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

            List<ScheduleEntry> scheduleEntries=new ArrayList<>();

            BigDecimal current_outstandingPrincipal=loanParameters.getPrincipal();

            //Till the month interest rate change is same schedule is as usual
            while(month<startMonth){
                ScheduleEntry scheduleEntry=new ScheduleEntry();
                scheduleEntry.setInstallmentNumber(month);
                scheduleEntry.setInstallmentAmount(installmentAmount);

                double interest=current_outstandingPrincipal.doubleValue()*rate;
                BigDecimal interestPaid=BigDecimal.valueOf(interest).setScale(0, RoundingMode.HALF_UP);
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

                month++;
            }

            BigDecimal interestRateChangeScenarioChangedRate=interestRateChangeScenario.getChangedRate();
            double changedRate=interestRateChangeScenarioChangedRate.doubleValue()/12/100;

            do{
                ScheduleEntry scheduleEntry=new ScheduleEntry();
                scheduleEntry.setInstallmentNumber(month);
                scheduleEntry.setInstallmentAmount(installmentAmount);

                if(month==startMonth)
                    rate=changedRate;
                double interest=current_outstandingPrincipal.doubleValue()*rate;
                BigDecimal interestPaid=BigDecimal.valueOf(interest).setScale(0, RoundingMode.HALF_UP);
                scheduleEntry.setInterest(interestPaid);

                //If final payment becomes less than installment amount
                BigDecimal finalPayment=current_outstandingPrincipal.add(interestPaid);
                if(finalPayment.compareTo(scheduleEntry.getInstallmentAmount())<=0){
                    scheduleEntry.setPrincipal(current_outstandingPrincipal);
                    scheduleEntry.setInstallmentAmount(finalPayment);
                    scheduleEntry.setOutstandingPrincipal(BigDecimal.valueOf(0));
                    scheduleEntries.add(scheduleEntry);
                    break;
                }

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
