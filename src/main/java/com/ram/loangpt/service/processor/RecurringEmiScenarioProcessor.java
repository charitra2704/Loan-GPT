package com.ram.loangpt.service.processor;

import com.ram.loangpt.dto.*;
import com.ram.loangpt.enums.FrequencyType;
import com.ram.loangpt.utils.FinanceUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chari
 **/
@Service
public class RecurringEmiScenarioProcessor implements ScenarioProcessor {
    public List<ScheduleEntry> processScenario(Scenario scenario,
                                    List<ScheduleEntry> schedule, LoanParameters loanParameters){
        if(scenario instanceof RecurringEmiChangeScenario recurringEmiChangeScenario) {

            BigDecimal changedEmiRate=recurringEmiChangeScenario.getEmiChangedRate().divide(BigDecimal.valueOf(100));
            FrequencyType frequencyType=recurringEmiChangeScenario.getFrequencyType();

            int frequencyFactor = switch (frequencyType) {
                case MONTHLY -> 1;
                case QUARTERLY -> 3;
                case ANNUALLY -> 12;
                case SEMI_ANNUALLY -> 6;
            };

            BigDecimal currentOutstandingPrincipal = BigDecimal.ZERO;

            List<ScheduleEntry> response = new ArrayList<>();
            BigDecimal currentEmi = BigDecimal.ZERO;
            currentEmi = schedule.getFirst().getInstallmentAmount();
            for (ScheduleEntry sc: schedule) {
                if(sc.getInstallmentNumber()<scenario.getStartMonth()) {
                    currentEmi = sc.getInstallmentAmount();
                } else  {
                    if (((sc.getInstallmentNumber()) - (recurringEmiChangeScenario.getStartMonth())) % frequencyFactor == 0) {
                        sc.setInstallmentAmount(FinanceUtil.roundOff(currentEmi.add(currentEmi.multiply(changedEmiRate))));
                        currentEmi = sc.getInstallmentAmount();
                    } else {
                        sc.setInstallmentAmount(currentEmi);
                    }
                    sc.setInterest(FinanceUtil.calculateInterest(currentOutstandingPrincipal, loanParameters.getInterestRate()));
                    sc.setPrincipal(sc.getInstallmentAmount().subtract(sc.getInterest()));
                    BigDecimal finalPayment=currentOutstandingPrincipal.add(sc.getInterest());
                    if (finalPayment.compareTo(sc.getInstallmentAmount().add(sc.getExtraPayment()))<=0) {
                        sc.setPrincipal(currentOutstandingPrincipal);
                        sc.setInstallmentAmount(finalPayment);
                        sc.setOutstandingPrincipal(BigDecimal.ZERO);
                        response.add(sc);
                        break;
                    }
                    sc.setOutstandingPrincipal(currentOutstandingPrincipal.subtract(sc.getPrincipal().add(sc.getExtraPayment())));
                }
                currentOutstandingPrincipal = sc.getOutstandingPrincipal();

                response.add(sc);
            }
            return response;
        }else {
            throw new IllegalArgumentException("Invalid Scenario");
        }
    }
}
