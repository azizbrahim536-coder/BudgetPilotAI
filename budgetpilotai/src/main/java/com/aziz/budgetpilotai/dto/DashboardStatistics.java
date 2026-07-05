package com.aziz.budgetpilotai.dto;

import java.math.BigDecimal;

public record DashboardStatistics(

        Integer year,

        Integer month,

        BigDecimal totalIncome,

        BigDecimal totalExpense,

        BigDecimal balance,

        long transactionCount,

        BigDecimal totalBudget,

        BigDecimal remainingBudget,

        boolean budgetExceeded

) {
}