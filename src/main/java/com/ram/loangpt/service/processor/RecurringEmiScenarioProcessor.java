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
public class RecurringEmiScenarioProcessor implements ScenarioProcessor {
    public Schedule processScenario(Scenario scenario,
                                    Schedule schedule, LoanParameters loanParameters){
        if(scenario instanceof RecurringEmiChangeScenario recurringEmiChangeScenario) {


            int startMonth=scenario.getStartMonth();
            int month=1;

            double rate = loanParameters.getInterestRate().doubleValue() / 12 / 100;

            List<ScheduleEntry> scheduleEntries=schedule.getSchedule();


            //Till the month Recurring emi payment is not applied schedule is as usual
            while(month<startMonth){
                month++;
            }

            double emiRateChange=recurringEmiChangeScenario.getPercentage().doubleValue()/100;
            FrequencyType frequencyType=recurringEmiChangeScenario.getFrequencyType();
            BigDecimal current_outstandingPrincipal=scheduleEntries.get(month-2).getOutstandingPrincipal();


            switch(frequencyType){
                case FrequencyType.MONTHLY -> {

                    do{
                        ScheduleEntry scheduleEntry=scheduleEntries.get(month-1);
                        scheduleEntry.setInstallmentNumber(month);

                        double value=scheduleEntries.get(month-2).getInstallmentAmount().doubleValue();
                        value+=(value*emiRateChange);
                        BigDecimal installmentAmount=BigDecimal.valueOf(value)
                                .setScale(0, RoundingMode.HALF_UP);
                        scheduleEntry.setInstallmentAmount(installmentAmount);

                        double interest=scheduleEntries.get(month-2).getOutstandingPrincipal().doubleValue()*rate;
                        BigDecimal interestPaid=BigDecimal.valueOf(interest).setScale(0, RoundingMode.HALF_UP);
                        scheduleEntry.setInterest(interestPaid);

                        //If final payment becomes less than installment amount
                        BigDecimal finalPayment=scheduleEntries.get(month-2).getOutstandingPrincipal().add(interestPaid);
                        if(finalPayment.compareTo(scheduleEntry.getInstallmentAmount())<=0){
                            scheduleEntry.setPrincipal(scheduleEntries.get(month-2).getOutstandingPrincipal());
                            scheduleEntry.setInstallmentAmount(finalPayment);
                            scheduleEntry.setOutstandingPrincipal(BigDecimal.valueOf(0));
                            scheduleEntries.add(scheduleEntry);
                            break;
                        }

                        double principal_entry=scheduleEntry.getInstallmentAmount().doubleValue()-interest;
                        BigDecimal principalPaid=BigDecimal.valueOf(principal_entry).
                                setScale(0,RoundingMode.HALF_UP);
                        scheduleEntry.setPrincipal(principalPaid);

                        double outstandingPrincipal=scheduleEntry.getOutstandingPrincipal().doubleValue()
                                -principal_entry;
                        BigDecimal outstandingPrincipal_entry=BigDecimal.valueOf(outstandingPrincipal).
                                setScale(0,RoundingMode.HALF_UP);
                        scheduleEntry.setOutstandingPrincipal(outstandingPrincipal_entry);
                        current_outstandingPrincipal=scheduleEntries.get(month-1).getOutstandingPrincipal();

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

                        if((month-startMonth)%3==0){
                            double value=scheduleEntries.get(month-2).getInstallmentAmount().doubleValue();
                            value+=(value*emiRateChange);
                            BigDecimal installmentAmount=BigDecimal.valueOf(value)
                                    .setScale(0, RoundingMode.HALF_UP);
                            scheduleEntry.setInstallmentAmount(installmentAmount);
                        }else {
                            scheduleEntry.setInstallmentAmount(scheduleEntries.get(month-2).getInstallmentAmount());
                        }

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

                        double outstandingPrincipal=current_outstandingPrincipal.doubleValue()-principal_entry-scheduleEntry
                                .getExtraPayment().doubleValue();
                        BigDecimal outstandingPrincipal_entry=BigDecimal.valueOf(outstandingPrincipal).
                                setScale(0,RoundingMode.HALF_UP);
                        scheduleEntry.setOutstandingPrincipal(outstandingPrincipal_entry);
                        current_outstandingPrincipal=scheduleEntries.get(month-1).getOutstandingPrincipal();

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

                        if((month-startMonth)%12==0){
                            double value=scheduleEntries.get(month-2).getInstallmentAmount().doubleValue();
                            value+=(value*emiRateChange);
                            BigDecimal installmentAmount=BigDecimal.valueOf(value)
                                    .setScale(0, RoundingMode.HALF_UP);
                            scheduleEntry.setInstallmentAmount(installmentAmount);
                        }else {
                            scheduleEntry.setInstallmentAmount(scheduleEntries.get(month-2).getInstallmentAmount());
                        }

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

                        double outstandingPrincipal=current_outstandingPrincipal.doubleValue()-principal_entry-scheduleEntry
                                .getExtraPayment().doubleValue();
                        BigDecimal outstandingPrincipal_entry=BigDecimal.valueOf(outstandingPrincipal).
                                setScale(0,RoundingMode.HALF_UP);
                        scheduleEntry.setOutstandingPrincipal(outstandingPrincipal_entry);
                        current_outstandingPrincipal=scheduleEntries.get(month-1).getOutstandingPrincipal();

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

                        if((month-startMonth)%6==0){
                            double value=scheduleEntries.get(month-2).getInstallmentAmount().doubleValue();
                            value+=(value*emiRateChange);
                            BigDecimal installmentAmount=BigDecimal.valueOf(value)
                                    .setScale(0, RoundingMode.HALF_UP);
                            scheduleEntry.setInstallmentAmount(installmentAmount);
                        }else {
                            scheduleEntry.setInstallmentAmount(scheduleEntries.get(month-2).getInstallmentAmount());
                        }

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

                        double outstandingPrincipal=current_outstandingPrincipal.doubleValue()-principal_entry-scheduleEntry
                                .getExtraPayment().doubleValue();
                        BigDecimal outstandingPrincipal_entry=BigDecimal.valueOf(outstandingPrincipal).
                                setScale(0,RoundingMode.HALF_UP);
                        scheduleEntry.setOutstandingPrincipal(outstandingPrincipal_entry);
                        current_outstandingPrincipal=scheduleEntries.get(month-1).getOutstandingPrincipal();

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
        }else {
            throw new IllegalArgumentException("Invalid Scenario");
        }
    }
}
