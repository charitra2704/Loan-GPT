# Loan GPT   

## Overview

A chat like interface which repsonds to loan queries in natural language and simulate prepayment scenarios.

## Problem Statement

User wants to
- Calculate monthly EMI
- Simulate prepayment scenerios
- Compare loan scenerios
- Evaluate Tenure Reduction
- Check affordability of loans

## Functional Requirements 
- Intent detection from natural language query
- Financial calculations like EMI, Prepayments 
- Generate amortization schedule 
- Compare prepayment scenerios
  - One time lumpsum additional payment
  - Recurring additional payment
  - Step up EMI
- Constraint/Edge case checking on financial inputs, prepayments etc.

## Non Functional Requirements

- Performance : All calculations should complete within 50ms
- Reliability : Loan calculations must maintain mathematical precision and should be deterministically testable
- Maintainability : Code should follow proper design principals so that individual components can be added / modified / removed confidently. 

## Design principals
- Extensibility
- Maintainablity
- Adhering to good design principals
  - REST design principals
  - Loose coupling
  - Separation of concerns using MVC
- Logging
- Deterministic calculations
- Financial Precision

## Rest API Design

### Resource Modeling

Core resources:
- loan - A loan with principal, interest and tenure. User can also ask for different scenarios as mentioned above.

## API Endpoints

### 1. Generate Amortization Schedule

#### Endpoint
```
POST /loan
```
#### Request
```json
{
  "principal": 5000000,
  "interestRate": 8.5,
  "tenureInMonths": 240,
}
```

#### Response
```json
// TODO
```

### 2. Simulate Scenerios

##### Endpoint
```
POST /loan
```
##### Request
```json
{
  "principal": 5000000,
  "interestRate": 8.5,
  "tenureInMonths": 240,

  "scenarioType": [
    {
      "scenarioType": "RECURRING_PREPAYMENT",
      "startMonth": 48,
      "amount": 25000,
      "frequencyType": "ANNUALLY"
    }
  ]
}
```

#### Response
```json
// TODO
```


### 3 Generate ammoritzation schedule and simulate scenarios using natural language
##### Endpoint
```
POST /chat
```
##### Request
``` Natural language
I have a loan of 3Cr for 20 years at 8% interest.
I want to pay 1 lakh extra every year.
```

#### Response
```json
// TODO
```

## Architecture
```
        Client(Customer)
              |
              v
    Controller Layer(ScheduleController)
              |
              v
    Services(ScheduleService) -------------------------------------------
 [If no scenarios]   /    \  [If Scnenarios]                            /
                    /      \                                           /
      Return Schedule    ScenarioProcessorFactory(Fetch scenario)     /
                                 |                                   /
                                 v                                  / 
                            Process Scenario                       /  
                                 |                                /
                                 v                               /
                              Generate Schedule -----------------
```








