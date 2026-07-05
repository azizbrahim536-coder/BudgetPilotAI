package com.aziz.budgetpilotai.controller;

import com.aziz.budgetpilotai.dto.BudgetRequest;
import com.aziz.budgetpilotai.entity.Budget;
import com.aziz.budgetpilotai.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(
            BudgetService budgetService
    ) {
        this.budgetService =
                budgetService;
    }

    @GetMapping
    public List<Budget> getBudgets(
            @RequestParam(required = false)
            Integer year,

            @RequestParam(required = false)
            Integer month
    ) {
        LocalDate today =
                LocalDate.now();

        return budgetService
                .getBudgets(
                        year == null
                                ? today.getYear()
                                : year,

                        month == null
                                ? today.getMonthValue()
                                : month
                );
    }

    @GetMapping("/{id}")
    public Budget getBudgetById(
            @PathVariable Long id
    ) {
        return budgetService
                .getBudgetById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Budget createBudget(
            @Valid
            @RequestBody
            BudgetRequest request
    ) {
        return budgetService
                .createBudget(request);
    }

    @PutMapping("/{id}")
    public Budget updateBudget(
            @PathVariable Long id,

            @Valid
            @RequestBody
            BudgetRequest request
    ) {
        return budgetService
                .updateBudget(
                        id,
                        request
                );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBudget(
            @PathVariable Long id
    ) {
        budgetService
                .deleteBudget(id);
    }
}