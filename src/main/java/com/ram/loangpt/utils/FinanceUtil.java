package com.ram.loangpt.utils;

import com.ram.loangpt.dto.ScheduleEntry;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author chari
 **/
public class FinanceUtil {

    public static BigDecimal roundOff(BigDecimal val) {
        return val.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal pmt(BigDecimal principal,  BigDecimal annualInterestRate, int numberOfMonths) {
        if(principal.compareTo(BigDecimal.ZERO)<0)
            throw new IllegalArgumentException("Principal cannot be negative: "+principal);

        if(numberOfMonths<=0)
            throw new IllegalArgumentException("Tenure should be a whole number: "+numberOfMonths);

        if(annualInterestRate.equals(BigDecimal.ZERO))
            return roundOff(principal.divide(BigDecimal.valueOf(numberOfMonths),MathContext.DECIMAL128));
        else if(annualInterestRate.compareTo(BigDecimal.ZERO)<0)
            throw new IllegalArgumentException("Interest rate cannot be negative: "+annualInterestRate);
        else {
            BigDecimal monthlyInterestRate = annualInterestRate.divide(BigDecimal.valueOf(12).multiply(BigDecimal.valueOf(100)), MathContext.DECIMAL128);
            BigDecimal numerator = principal.multiply(monthlyInterestRate).multiply((monthlyInterestRate.add(BigDecimal.ONE)).pow(numberOfMonths));
            BigDecimal denominator = ((monthlyInterestRate.add(BigDecimal.ONE)).pow(numberOfMonths)).subtract(BigDecimal.ONE);
            BigDecimal installmentAmount = numerator.divide(denominator, MathContext.DECIMAL128);
            return roundOff(installmentAmount);
        }
    }

    public static BigDecimal calculateInterest(BigDecimal principal,  BigDecimal annualInterestRate) {
        BigDecimal monthlyInterestRate=annualInterestRate.divide(BigDecimal.valueOf(12).multiply(BigDecimal.valueOf(100)),MathContext.DECIMAL128);
        BigDecimal interestPaid = principal.multiply(monthlyInterestRate);
        return roundOff(interestPaid);
    }

}
