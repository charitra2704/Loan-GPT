package com.ram.loangpt.dto;

import java.util.List;

/**
 * @author chari
 **/
public class LoanRequest {

    private LoanParameters loanParameters;
    private List<Scenario> scenarios;

    public LoanParameters getLoanParameters() {
        return loanParameters;
    }

    public void setLoanParameters(LoanParameters loanParameters) {
        this.loanParameters = loanParameters;
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }
}
