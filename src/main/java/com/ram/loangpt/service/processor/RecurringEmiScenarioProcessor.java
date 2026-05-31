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

            //Till the month Recurring emi payment is not applied schedule is as usual
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

            double emiRateChange=recurringEmiChangeScenario.getPercentage().doubleValue()/100;
            FrequencyType frequencyType=recurringEmiChangeScenario.getFrequencyType();


            switch(frequencyType){
                case FrequencyType.MONTHLY -> {

                    do{
                        ScheduleEntry scheduleEntry=new ScheduleEntry();
                        scheduleEntry.setInstallmentNumber(month);

                        value+=(value*emiRateChange);
                        installmentAmount=BigDecimal.valueOf(value)
                                .setScale(0, RoundingMode.HALF_EVEN);
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
                    while(current_outstandingPrincipal.compareTo(BigDecimal.ZERO)>0);

                    schedule.setSchedule(scheduleEntries);
                    return schedule;

                }
                case FrequencyType.QUARTERLY -> {

                    do{
                        ScheduleEntry scheduleEntry=new ScheduleEntry();
                        scheduleEntry.setInstallmentNumber(month);
                        if((month-startMonth)%3==0){
                            value+=(value*emiRateChange);
                            installmentAmount=BigDecimal.valueOf(value)
                                    .setScale(0, RoundingMode.HALF_EVEN);
                        }
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
                    while(current_outstandingPrincipal.compareTo(BigDecimal.ZERO)>0);

                    schedule.setSchedule(scheduleEntries);
                    return schedule;

                }
                case FrequencyType.ANNUALLY -> {

                    do{
                        ScheduleEntry scheduleEntry=new ScheduleEntry();
                        scheduleEntry.setInstallmentNumber(month);
                        if((month-startMonth)%12==0){
                            value+=(value*emiRateChange);
                            installmentAmount=BigDecimal.valueOf(value)
                                    .setScale(0, RoundingMode.HALF_EVEN);
                        }
                        scheduleEntry.setInstallmentAmount(installmentAmount);

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
                    return schedule;

                }
                case FrequencyType.SEMI_ANNUALLY -> {

                    do{
                        ScheduleEntry scheduleEntry=new ScheduleEntry();
                        scheduleEntry.setInstallmentNumber(month);

                        if((month-startMonth)%6==0) {
                            value+=(value*emiRateChange);
                            installmentAmount=BigDecimal.valueOf(value)
                                    .setScale(0, RoundingMode.HALF_EVEN);
                        }
                        scheduleEntry.setInstallmentAmount(installmentAmount);

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
