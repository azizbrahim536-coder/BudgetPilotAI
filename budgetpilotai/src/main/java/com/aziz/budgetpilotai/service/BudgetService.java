package com.aziz.budgetpilotai.service;

import com.aziz.budgetpilotai.dto.BudgetRequest;
import com.aziz.budgetpilotai.entity.Budget;
import com.aziz.budgetpilotai.entity.Category;
import com.aziz.budgetpilotai.entity.TransactionType;
import com.aziz.budgetpilotai.repository.BudgetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    private final CategoryService categoryService;

    public BudgetService(
            BudgetRepository budgetRepository,
            CategoryService categoryService
    ) {
        this.budgetRepository =
                budgetRepository;

        this.categoryService =
                categoryService;
    }

    public List<Budget> getBudgets(
            Integer year,
            Integer month
    ) {
        return budgetRepository
                .findByBudgetYearAndBudgetMonthOrderByCategoryNameAsc(
                        year,
                        month
                );
    }

    public Budget getBudgetById(
            Long id
    ) {
        return budgetRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Budget introuvable"
                                )
                );
    }

    public Budget createBudget(
            BudgetRequest request
    ) {
        Category category =
                categoryService
                        .getCategoryById(
                                request.categoryId()
                        );

        validateExpenseCategory(
                category
        );

        if (
                budgetRepository
                        .existsByCategoryIdAndBudgetYearAndBudgetMonth(
                                category.getId(),
                                request.budgetYear(),
                                request.budgetMonth()
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Un budget existe déjà pour cette période"
            );
        }

        Budget budget =
                new Budget();

        applyRequest(
                budget,
                request,
                category
        );

        return budgetRepository
                .save(budget);
    }

    public Budget updateBudget(
            Long id,
            BudgetRequest request
    ) {
        Budget budget =
                getBudgetById(id);

        Category category =
                categoryService
                        .getCategoryById(
                                request.categoryId()
                        );

        validateExpenseCategory(
                category
        );

        applyRequest(
                budget,
                request,
                category
        );

        return budgetRepository
                .save(budget);
    }

    public void deleteBudget(
            Long id
    ) {
        budgetRepository.delete(
                getBudgetById(id)
        );
    }

    private void applyRequest(
            Budget budget,
            BudgetRequest request,
            Category category
    ) {
        budget.setCategory(category);

        budget.setLimitAmount(
                request.limitAmount()
        );

        budget.setBudgetYear(
                request.budgetYear()
        );

        budget.setBudgetMonth(
                request.budgetMonth()
        );
    }

    private void validateExpenseCategory(
            Category category
    ) {
        if (
                category.getType()
                        != TransactionType.EXPENSE
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le budget doit utiliser une catégorie EXPENSE"
            );
        }
    }
}