package com.aziz.budgetpilotai.service;

import com.aziz.budgetpilotai.dto.DashboardStatistics;
import com.aziz.budgetpilotai.entity.TransactionType;
import com.aziz.budgetpilotai.repository.BudgetRepository;
import com.aziz.budgetpilotai.repository.FinancialTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class StatisticsService {

    private final FinancialTransactionRepository
            transactionRepository;

    private final BudgetRepository
            budgetRepository;

    public StatisticsService(
            FinancialTransactionRepository transactionRepository,
            BudgetRepository budgetRepository
    ) {
        this.transactionRepository =
                transactionRepository;

        this.budgetRepository =
                budgetRepository;
    }

    public DashboardStatistics getDashboard(
            Integer year,
            Integer month
    ) {
        YearMonth period =
                YearMonth.of(
                        year,
                        month
                );

        LocalDate startDate =
                period.atDay(1);

        LocalDate endDate =
                period.atEndOfMonth();

        BigDecimal income =
                transactionRepository
                        .sumByTypeAndPeriod(
                                TransactionType.INCOME,
                                startDate,
                                endDate
                        );

        BigDecimal expense =
                transactionRepository
                        .sumByTypeAndPeriod(
                                TransactionType.EXPENSE,
                                startDate,
                                endDate
                        );

        BigDecimal balance =
                income.subtract(expense);

        long transactionCount =
                transactionRepository
                        .countByTransactionDateBetween(
                                startDate,
                                endDate
                        );

        BigDecimal totalBudget =
                budgetRepository
                        .sumByPeriod(
                                year,
                                month
                        );

        BigDecimal remainingBudget =
                totalBudget.subtract(expense);

        boolean exceeded =
                totalBudget.signum() > 0
                        &&
                        expense.compareTo(
                                totalBudget
                        ) > 0;

        return new DashboardStatistics(
                year,
                month,
                income,
                expense,
                balance,
                transactionCount,
                totalBudget,
                remainingBudget,
                exceeded
        );
    }
}