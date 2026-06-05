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
public class RecurringPaymentScenarioProcessor implements ScenarioProcessor {
    public List<ScheduleEntry> processScenario(Scenario scenario,
                                               List<ScheduleEntry> schedule, LoanParameters loanParameters){
        if(scenario instanceof RecurringPaymentScenario recurringPaymentScenario) {

            FrequencyType frequencyType=recurringPaymentScenario.getFrequencyType();

            int frequencyFactor = switch (frequencyType) {
                case MONTHLY -> 1;
                case QUARTERLY -> 3;
                case ANNUALLY -> 12;
                case SEMI_ANNUALLY -> 6;
            };
                    BigDecimal currentOutstandingPrincipal = BigDecimal.ZERO;
                    List<ScheduleEntry> response = new ArrayList<>();

                    for (ScheduleEntry sc: schedule) {
                        if(sc.getInstallmentNumber()<scenario.getStartMonth()) {
                            currentOutstandingPrincipal = sc.getOutstandingPrincipal();
                        }
                        else {
                            if (((sc.getInstallmentNumber()) - (recurringPaymentScenario.getStartMonth())) % frequencyFactor == 0) {
                                sc.setExtraPayment(sc.getExtraPayment().add(recurringPaymentScenario.getAmount()));
                            }

                            sc.setInterest(FinanceUtil.calculateInterest(currentOutstandingPrincipal, loanParameters.getInterestRate()));
                            sc.setPrincipal(sc.getInstallmentAmount().subtract(sc.getInterest()));
                            sc.setOutstandingPrincipal(
                                    currentOutstandingPrincipal.subtract(sc.getPrincipal().add(sc.getExtraPayment()))
                            );

                            BigDecimal finalPayment = currentOutstandingPrincipal.add(sc.getInterest());
                            if (finalPayment.compareTo(sc.getInstallmentAmount().add(sc.getExtraPayment())) <= 0) {
                                sc.setPrincipal(currentOutstandingPrincipal);
                                sc.setInstallmentAmount(finalPayment);
                                sc.setOutstandingPrincipal(BigDecimal.ZERO);
                                response.add(sc);
                                break;
                            }
                            currentOutstandingPrincipal = sc.getOutstandingPrincipal();
                            }
                        response.add(sc);
                        }
            return response;
        }
        else {
            throw new IllegalArgumentException("Invalid Scenario");
        }
    }
}
