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
public class LumpsumPaymentScenarioProcessor implements ScenarioProcessor {
    public Schedule processScenario(Scenario scenario, Schedule schedule, LoanParameters loanParameters){
        if(scenario instanceof LumpSumPaymentScenario lumpSumPaymentScenario) {
            int startMonth=scenario.getStartMonth();
            int month=1;

            double rate = loanParameters.getInterestRate().doubleValue() / 12 / 100;
            List<ScheduleEntry> scheduleEntries=schedule.getSchedule();

            //Till the month Lump Sum payment is made schedule is as usual
            while(month<startMonth){
                month++;
            }

            BigDecimal current_outstandingPrincipal=scheduleEntries.get(month-2).getOutstandingPrincipal();

            //Additional Lump Sum payment reduces outstanding principal
            BigDecimal lumpSumAmount=lumpSumPaymentScenario.getAmount();

            do{
                ScheduleEntry scheduleEntry=scheduleEntries.get(month-1);
                scheduleEntry.setInstallmentNumber(month);
                scheduleEntry.setInstallmentAmount(schedule.getInstallmentAmount());

                double interest=current_outstandingPrincipal.doubleValue()*rate;
                BigDecimal interestPaid=BigDecimal.valueOf(interest).setScale(0, RoundingMode.HALF_UP);
                scheduleEntry.setInterest(interestPaid);

                //If final payment becomes less than installment amount
                BigDecimal finalPayment=current_outstandingPrincipal.add(interestPaid);
                if(finalPayment.compareTo(scheduleEntry.getInstallmentAmount())<=0){
                    scheduleEntry.setPrincipal(current_outstandingPrincipal);
                    scheduleEntry.setInstallmentAmount(finalPayment);
                    scheduleEntry.setOutstandingPrincipal(BigDecimal.valueOf(0));
                    break;
                }

                double principal_entry=scheduleEntry.getInstallmentAmount().doubleValue()-interest;
                BigDecimal principalPaid=BigDecimal.valueOf(principal_entry).
                        setScale(2,RoundingMode.HALF_UP);
                scheduleEntry.setPrincipal(principalPaid);

                double outstandingPrincipal=current_outstandingPrincipal.doubleValue()-principal_entry;
                BigDecimal outstandingPrincipal_entry=BigDecimal.valueOf(outstandingPrincipal).
                        setScale(2,RoundingMode.HALF_UP);
                if(month==startMonth) {
                    outstandingPrincipal_entry = outstandingPrincipal_entry.subtract(lumpSumAmount);
                    scheduleEntry.setExtraPayment(lumpSumAmount);
                }else{
                    scheduleEntry.setExtraPayment(BigDecimal.ZERO);
                }
                scheduleEntry.setOutstandingPrincipal(outstandingPrincipal_entry);
                current_outstandingPrincipal=scheduleEntry.getOutstandingPrincipal();

                scheduleEntries.add(scheduleEntry);

                month++;
            }
            while(current_outstandingPrincipal.compareTo(BigDecimal.ZERO)>0);

            schedule.setSchedule(scheduleEntries);

        } else {
            throw new IllegalArgumentException("Scenario Mismatch");
        }
        return schedule;
    }
}
