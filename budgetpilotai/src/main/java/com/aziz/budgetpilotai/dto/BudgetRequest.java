package com.aziz.budgetpilotai.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record BudgetRequest(

        @NotNull
        Long categoryId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal limitAmount,

        @NotNull
        @Min(2000)
        @Max(2100)
        Integer budgetYear,

        @NotNull
        @Min(1)
        @Max(12)
        Integer budgetMonth

) {
}