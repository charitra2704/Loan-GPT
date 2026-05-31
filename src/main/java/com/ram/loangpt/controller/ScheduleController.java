package com.ram.loangpt.controller;

import com.ram.loangpt.dto.LoanRequest;
import com.ram.loangpt.dto.Schedule;
import com.ram.loangpt.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chari
 **/

@RestController
@RequestMapping("/loan")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public Schedule generateSchedule(@RequestBody LoanRequest loanRequest) {
        return scheduleService.generateSchedule(loanRequest);
    }
}
