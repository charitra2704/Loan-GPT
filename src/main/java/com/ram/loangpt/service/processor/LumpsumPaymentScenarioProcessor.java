package com.ram.loangpt.service.processor;

import com.ram.loangpt.dto.*;
import com.ram.loangpt.utils.FinanceUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author chari
 **/
@Service
public class LumpsumPaymentScenarioProcessor implements ScenarioProcessor {
    public List<ScheduleEntry> processScenario(Scenario scenario, List<ScheduleEntry> schedule, LoanParameters loanParameters){
        if(scenario.getStartMonth()>schedule.size())
            throw new IllegalArgumentException("Start Month cannot exceed Tenure: "+scenario.getStartMonth());
        if(scenario instanceof LumpSumPaymentScenario lumpSumPaymentScenario) {
            if(lumpSumPaymentScenario.getAmount().compareTo(BigDecimal.ZERO)<0)
                throw new IllegalArgumentException("Lump Sum amount cannot be negative: "+lumpSumPaymentScenario.getAmount());
            BigDecimal currentOutstandingPrincipal = loanParameters.getPrincipal();
            List<ScheduleEntry> response = new ArrayList<>();
            for (ScheduleEntry sc: schedule) {
                if (sc.getInstallmentNumber().equals(scenario.getStartMonth())) {
                    sc.setExtraPayment(sc.getExtraPayment().add(lumpSumPaymentScenario.getAmount()));
                        BigDecimal finalPayment=currentOutstandingPrincipal.add(sc.getInterest());
                    if (finalPayment.compareTo(sc.getInstallmentAmount().add(sc.getExtraPayment()))<=0) {
                        sc.setPrincipal(currentOutstandingPrincipal);
                        sc.setInstallmentAmount(finalPayment);
                        sc.setOutstandingPrincipal(BigDecimal.ZERO);
                        response.add(sc);
                        break;
                    }
                    sc.setOutstandingPrincipal(
                            sc.getOutstandingPrincipal().subtract(lumpSumPaymentScenario.getAmount())
                    );
                }
                else if (sc.getInstallmentNumber() > scenario.getStartMonth()) {
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
        } else {
            throw new IllegalArgumentException("Scenario Mismatch");
        }
    }
}
