package com.ram.loangpt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.loangpt.dto.LoanParameters;
import com.ram.loangpt.dto.LoanRequest;
import com.ram.loangpt.dto.Schedule;
import com.ram.loangpt.service.ScheduleChatService;
import com.ram.loangpt.service.ScheduleService;
import jakarta.validation.constraints.Past;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author chari
 **/
@RestController
@RequestMapping("/chat")
public class    ScheduleChatController {

    private static final Logger log = LoggerFactory.getLogger(ScheduleChatController.class);
    private final ScheduleService scheduleService;
    private final ScheduleChatService scheduleChatService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ScheduleChatController(ScheduleService scheduleService, ScheduleChatService scheduleChatService) {
        this.scheduleService = scheduleService;
        this.scheduleChatService=scheduleChatService;
    }

    @PostMapping
    public Schedule generateSchedule(@RequestBody String userInput) throws Exception {

        LoanRequest loanRequest = scheduleChatService.generateResponse(userInput);
        log.info(objectMapper.writeValueAsString(loanRequest));
        return scheduleService.generateSchedule(loanRequest);
    }
}
