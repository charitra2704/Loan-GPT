# Loan GPT - LLD   

## Overview

A Chat like interface which repsonds to loan queries in natural language

Supports:

- EMI calcultion
- Generate amortization schedule
- Simulate prepayment scenerios
- Loan comparision scenerios
- Floating interest simulations

Additional Future enhancements:
- Provide Tax and financial planning
- Notify and send alerts for due dates
- Credit score intelligence

The design prioritizes:
- Extensibility
- Maintainablity
- REST correctness
- Auditibility
- Deterministic calculations
- Financial Precision
- Loose coupling
- Separation of concerns using MVC


## Problem Statement

User wants to:

- Calculate monthly EMI
- Simulate prepayment scenerios
- Compare loan scenerios
- Evaluate Tenure Reduction
- Check affordability of loans
- Analyze risks

## Functional Requirements 

- Intent detection from natural language query
- Financial calculations like EMI, Prepayments 
- Generate amortization schedule 
- Compare prepayment scenerios
- Constraint/Edge case checking on financial inputs, prepayments etc
- Natural language resposne to user

## Core Features

### Loan Schedule Generation

Generate Amortization schedule for:

- Home Loans
- Business Loans
- Vehicle Loans
- Education Loans
- Personal Loans

### EMI Calculation

Calculate:

- EMI
- Total interest
- Total repayment 

### Prepayment Support

Support:

- one-time prepayment
- recurring extra payment

### Variable Interest Rate

Support rate changes during tenure.

### Loan Comparison

Compare:

- different tenures
- different rates
- different prepayment strategies

## Non Functional Requirements

- Performance : All calculations should complete within 4 seconds and other non mathematical responses should generate within 2 second with stable internet connection
- Security : Calculations should be auditible and financial data must be encrypted
- Scalability : System should support increased traffic without breaking down
- Reliability : Loan calculations must maintain mathematical precision 
- Maintainability : MVC architecture should be followed with clear separation of concerns
- Testability : Each component must be independently tested 

## Rest API Design
### Resource Modeling

Core resources:

- /chat --> NLP and intent detection
- /calculate --> Calculate EMI, Interest amount 
- /schedules --> Amortization Schedule
- /comparisions --> Compare loans, strategies
- /simulations --> Simulate prepayment, tenure reduction, Interest rate change

Future:
- /notifications --> Notify users about due date 
- /credit-score --> Credit score intelligence

## API Endpoints
### 1. Calculate EMI and Interest amount
#### 1.1 Calculate EMI 

##### Endpoint
```
POST /loan/calculate/emi
```
##### Request
```json
{
  "loanAmount": 5000000,
  "annualInterestRate": 8.5,
  "tenureMonths": 240,
  "startDate": "2026-06-01",
  "emiType": "REDUCING_BALANCE",
}
```
#### 1.2 Calculate Interest amount

##### Endpoint
```
POST /loan/calculate/interest
```
##### Request
```json
{
  "EMI": 50000,
  "Principal    ": 1000000,
  "tenureMonths": 24,
  "startDate": "2026-06-01",
  "emiType": "REDUCING_BALANCE",
}
```

### 2. Generate Amortization Schedule

#### Endpoint
```
POST /loan/schedules
```
#### Request
```json
{
  "loanAmount": 5000000,
  "annualInterestRate": 8.5,
  "tenureMonths": 240,
  "startDate": "2026-06-01",
  "emiType": "REDUCING_BALANCE",
  "prepayments": [
    {
      "month": 12,
      "amount": 200000
    }
  ]
}
```

### 3. Fetch Existing Schedule

#### Endpoint
```
GET /loan/schedules/{scheduleId}
```

### 4. Compare Loan Scenarios

#### Endpoint
```
POST /loan/comparisons
```
#### Request
```json
{
  "baseLoan": {
    "loanAmount": 5000000,
    "annualInterestRate": 8.5,
    "tenureMonths": 240
  },
  "scenarios": [
    {
      "name": "15 Year Loan",
      "tenureMonths": 180
    },
    {
      "name": "Extra EMI",
      "monthlyExtraPayment": 10000
    }
  ]
}
```

### 5. Simulate Prepayment Strategies, Interest rate change and tenure reduction
#### 5.1 Prepayment simulation

##### Endpoint
```
POST /simulations/prepayment
```
##### Request
```json
{
  "loanType": "HOME_LOAN",
  "principal": 3000000,
  "annualInterestRate": 9.2,
  "tenureMonths": 180,

  "prepayment": {
    "type": "RECURRING",
    "monthlyExtraPayment": 10000,
    "startMonth": 12
  }
}
```

#### 5.2 Floating Interest Rate Simulation

##### Endpoint
```
POST /simulations/floating-rate
```
##### Request
```json
{
  "principal": 4000000,
  "initialInterestRate": 7.8,
  "tenureMonths": 240,

  "rateChanges": [
    {
      "month": 12,
      "newRate": 8.4
    },
    {
      "month": 36,
      "newRate": 9.1
    }
  ]
}
```

#### 5.3 Tenure Reduction Simulation

##### Endpoint
```
POST /simulations/tenure-reduction
```
##### Request
```json
{
  "principal": 2500000,
  "annualInterestRate": 8.0,
  "currentEMI": 25000,

  "strategy": {
    "extraMonthlyPayment": 5000
  }
}
```

## Domain Model

### Loan

```java
class Loan {
    UUID loanId;
    BigDecimal principal;
    BigDecimal annualInterestRate;
    Integer tenureMonths;
    LoanType loanType;
    EmiType emiType;
}
```

### Schedule Entry
```java
class ScheduleEntry {
    Integer month;
    LocalDate paymentDate;

    BigDecimal openingBalance;
    BigDecimal emi;
    BigDecimal principalComponent;
    BigDecimal interestComponent;
    BigDecimal closingBalance;
}
```

### Prepayment
```java
class Prepayment {
    Integer month;
    BigDecimal amount;
}
```
### Loan Simulations
```java
class LoanSimulation {
    Loan loan;
    List<Prepayment> prepayments;
    List<InterestRateChange> rateChanges;
    SimulationStrategy strategy;
}
```
### InterestRateChange
```java
class InterestRateChange {
    Integer month;
    BigDecimal newInterestRate;
}
```
### SimulationStrategy
```java
enum SimulationStrategy {
    REDUCE_EMI,
    REDUCE_TENURE
}
```

### LoanComparison
```java
class LoanComparison {
    List<Loan> loans;
    ComparisonCriteria criteria;
}
```

### Architecture
```
Client
  |
  v
Controller Layer
  |
  v
Services
  |
  v
Calculation Engine
  |
  v
Domain Models
```

## Precision Handling
Financial calculations must:

- use BigDecimal
- avoid floating-point arithmetic
- use banker's rounding

## Validation Rules
| Validation        | Rule                      |
| ----------------- | ------------------------- |
| loanAmount        | > 0                       |
| interestRate      | 0-100                     |
| tenureMonths      | > 0                       |
| prepayment amount | > 0                       |

## HTTP Status Codes
| Status | Meaning            |
| ------ | ------------------ |
| 200    | success            |
| 201    | created            |
| 400    | validation failure |
| 404    | schedule not found |
| 409    | duplicate request  |








