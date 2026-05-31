package com.ram.loangpt.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chari
 **/
@JsonPropertyOrder({
        "installmentAmount",
        "totalInterestPayable",
        "totalPayment",
        "schedule"
})
public class Schedule {
    private BigDecimal installmentAmount;
    private BigDecimal totalInterestPayable;
    private BigDecimal totalPayment;
    private List<ScheduleEntry> schedule;

    public BigDecimal getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(BigDecimal installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public BigDecimal getTotalInterestPayable() {
        return totalInterestPayable;
    }

    public void setTotalInterestPayable(BigDecimal totalInterestPayable) {
        this.totalInterestPayable = totalInterestPayable;
    }

    public BigDecimal getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(BigDecimal totalPayment) {
        this.totalPayment = totalPayment;
    }

    public List<ScheduleEntry> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<ScheduleEntry> schedule) {
        this.schedule = schedule;
    }
}
