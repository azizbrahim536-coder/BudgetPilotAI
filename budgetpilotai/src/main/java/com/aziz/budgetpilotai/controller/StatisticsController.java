package com.aziz.budgetpilotai.controller;

import com.aziz.budgetpilotai.dto.DashboardStatistics;
import com.aziz.budgetpilotai.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService
            statisticsService;

    public StatisticsController(
            StatisticsService statisticsService
    ) {
        this.statisticsService =
                statisticsService;
    }

    @GetMapping("/dashboard")
    public DashboardStatistics getDashboard(
            @RequestParam(required = false)
            Integer year,

            @RequestParam(required = false)
            Integer month
    ) {
        LocalDate today =
                LocalDate.now();

        return statisticsService
                .getDashboard(
                        year == null
                                ? today.getYear()
                                : year,

                        month == null
                                ? today.getMonthValue()
                                : month
                );
    }
}