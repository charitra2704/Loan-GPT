package com.ram.loangpt.service.processor;

import com.ram.loangpt.dto.*;
import com.ram.loangpt.enums.FrequencyType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chari
 **/
@Service
public class RecurringPaymentScenarioProcessor implements ScenarioProcessor {
    public Schedule processScenario(Scenario scenario,
                                    Schedule schedule, LoanParameters loanParameters){
        if(scenario instanceof RecurringPaymentScenario recurringPaymentScenario) {

            int startMonth=scenario.getStartMonth();
            int month=1;

            double rate = loanParameters.getInterestRate().doubleValue() / 12 / 100;

            List<ScheduleEntry> scheduleEntries=schedule.getSchedule();

            //Till the month Recurring payment starts schedule is as usual
            while(month<startMonth){
                scheduleEntries.get(month-1).setExtraPayment(BigDecimal.ZERO);
                month++;
            }

            BigDecimal current_outstandingPrincipal=scheduleEntries.get(month-2).getOutstandingPrincipal();
            BigDecimal recurringAmount=recurringPaymentScenario.getAmount();
            FrequencyType frequencyType=recurringPaymentScenario.getFrequencyType();

            switch(frequencyType){
                case FrequencyType.MONTHLY -> {

                    do{
                        ScheduleEntry scheduleEntry=scheduleEntries.get(month-1);
                        scheduleEntry.setInstallmentNumber(month);
                        scheduleEntry.setInstallmentAmount(scheduleEntry.getInstallmentAmount());
                        scheduleEntry.setExtraPayment(recurringAmount);

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

                        double principal_entry=scheduleEntry.getInstallmentAmount().doubleValue()-interest;
                        BigDecimal principalPaid=BigDecimal.valueOf(principal_entry).
                                setScale(0,RoundingMode.HALF_UP);
                        scheduleEntry.setPrincipal(principalPaid);

                        current_outstandingPrincipal=current_outstandingPrincipal.subtract(recurringAmount);
                        current_outstandingPrincipal=current_outstandingPrincipal.subtract(principalPaid);
                        scheduleEntry.setOutstandingPrincipal(current_outstandingPrincipal);
                        current_outstandingPrincipal=scheduleEntry.getOutstandingPrincipal();

                        month++;
                    }
                    while(current_outstandingPrincipal.compareTo(BigDecimal.ZERO)>0);

                    schedule.setSchedule(scheduleEntries);
                    return schedule;

                }
                case FrequencyType.QUARTERLY -> {

                    do{
                        ScheduleEntry scheduleEntry=scheduleEntries.get(month-1);
                        scheduleEntry.setInstallmentNumber(month);
                        scheduleEntry.setInstallmentAmount(scheduleEntry.getInstallmentAmount());

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

                        double principal_entry=scheduleEntry.getInstallmentAmount().doubleValue()-interest;
                        BigDecimal principalPaid=BigDecimal.valueOf(principal_entry).
                                setScale(0,RoundingMode.HALF_UP);
                        scheduleEntry.setPrincipal(principalPaid);

                        if((month-startMonth)%3==0) {
                            current_outstandingPrincipal = current_outstandingPrincipal.subtract(recurringAmount);
                            scheduleEntry.setExtraPayment(recurringAmount);
                        }else{
                            scheduleEntry.setExtraPayment(BigDecimal.ZERO);
                        }
                        current_outstandingPrincipal=current_outstandingPrincipal.subtract(principalPaid);
                        scheduleEntry.setOutstandingPrincipal(current_outstandingPrincipal);
                        current_outstandingPrincipal=scheduleEntry.getOutstandingPrincipal();

                        month++;
                    }
                    while(current_outstandingPrincipal.compareTo(BigDecimal.ZERO)>0);

                    schedule.setSchedule(scheduleEntries);
                    return schedule;

                }
                case FrequencyType.ANNUALLY -> {

                    do{
                        ScheduleEntry scheduleEntry=scheduleEntries.get(month-1);
                        scheduleEntry.setInstallmentNumber(month);
                        scheduleEntry.setInstallmentAmount(scheduleEntry.getInstallmentAmount());

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
                                setScale(0,RoundingMode.HALF_UP);
                        scheduleEntry.setPrincipal(principalPaid);

                        if((month-startMonth)%12==0) {
                            current_outstandingPrincipal = current_outstandingPrincipal.subtract(recurringAmount);
                            scheduleEntry.setExtraPayment(recurringAmount);
                        }else{
                            scheduleEntry.setExtraPayment(BigDecimal.ZERO);
                        }
                        current_outstandingPrincipal=current_outstandingPrincipal.subtract(principalPaid);
                        scheduleEntry.setOutstandingPrincipal(current_outstandingPrincipal);
                        current_outstandingPrincipal=scheduleEntry.getOutstandingPrincipal();

                        month++;
                    }
                    while(current_outstandingPrincipal.compareTo(BigDecimal.ZERO)>0);

                    schedule.setSchedule(scheduleEntries);
                    return schedule;

                }
                case FrequencyType.SEMI_ANNUALLY -> {

                    do{
                        ScheduleEntry scheduleEntry=scheduleEntries.get(month-1);
                        scheduleEntry.setInstallmentNumber(month);
                        scheduleEntry.setInstallmentAmount(scheduleEntry.getInstallmentAmount());

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

                        double principal_entry=scheduleEntry.getInstallmentAmount().doubleValue()-interest;
                        BigDecimal principalPaid=BigDecimal.valueOf(principal_entry).
                                setScale(0,RoundingMode.HALF_UP);
                        scheduleEntry.setPrincipal(principalPaid);

                        if((month-startMonth)%6==0){
                            current_outstandingPrincipal=current_outstandingPrincipal.subtract(recurringAmount);
                            scheduleEntry.setExtraPayment(recurringAmount);
                        }else{
                            scheduleEntry.setExtraPayment(BigDecimal.ZERO);
                        }
                        current_outstandingPrincipal=current_outstandingPrincipal.subtract(principalPaid);
                        scheduleEntry.setOutstandingPrincipal(current_outstandingPrincipal);
                        current_outstandingPrincipal=scheduleEntry.getOutstandingPrincipal();

                        month++;
                    }
                    while(current_outstandingPrincipal.compareTo(BigDecimal.ZERO)>0);

                    schedule.setSchedule(scheduleEntries);
                    return schedule;

                }
                default -> {
                    throw new IllegalArgumentException("Invalid Frequency type "+frequencyType);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid Scenario");
        }
    }
}
