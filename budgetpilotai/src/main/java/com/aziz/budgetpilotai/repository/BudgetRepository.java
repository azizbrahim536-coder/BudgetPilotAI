package com.aziz.budgetpilotai.repository;

import com.aziz.budgetpilotai.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    List<Budget>
    findByBudgetYearAndBudgetMonthOrderByCategoryNameAsc(
            Integer budgetYear,
            Integer budgetMonth
    );

    boolean
    existsByCategoryIdAndBudgetYearAndBudgetMonth(
            Long categoryId,
            Integer budgetYear,
            Integer budgetMonth
    );

    long countByCategoryId(
            Long categoryId
    );

    @Query("""
        SELECT COALESCE(
            SUM(budget.limitAmount),
            0
        )
        FROM Budget budget
        WHERE budget.budgetYear = :year
        AND budget.budgetMonth = :month
        """)
    BigDecimal sumByPeriod(
            @Param("year")
            Integer year,

            @Param("month")
            Integer month
    );
}