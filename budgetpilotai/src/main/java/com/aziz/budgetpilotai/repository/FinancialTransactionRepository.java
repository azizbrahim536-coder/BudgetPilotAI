package com.aziz.budgetpilotai.repository;

import com.aziz.budgetpilotai.entity.FinancialTransaction;
import com.aziz.budgetpilotai.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FinancialTransactionRepository
        extends JpaRepository<
        FinancialTransaction,
        Long
        > {

    @Query("""
        SELECT transaction
        FROM FinancialTransaction transaction
        WHERE (
            :search IS NULL
            OR LOWER(transaction.title)
                LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(
                COALESCE(transaction.description, '')
            )
                LIKE LOWER(CONCAT('%', :search, '%'))
        )
        AND (
            :type IS NULL
            OR transaction.type = :type
        )
        AND (
            :categoryId IS NULL
            OR transaction.category.id = :categoryId
        )
        AND (
            :dateFrom IS NULL
            OR transaction.transactionDate >= :dateFrom
        )
        AND (
            :dateTo IS NULL
            OR transaction.transactionDate <= :dateTo
        )
        ORDER BY
            transaction.transactionDate DESC,
            transaction.id DESC
        """)
    List<FinancialTransaction>
    searchTransactions(
            @Param("search")
            String search,

            @Param("type")
            TransactionType type,

            @Param("categoryId")
            Long categoryId,

            @Param("dateFrom")
            LocalDate dateFrom,

            @Param("dateTo")
            LocalDate dateTo
    );

    @Query("""
        SELECT COALESCE(
            SUM(transaction.amount),
            0
        )
        FROM FinancialTransaction transaction
        WHERE transaction.type = :type
        AND transaction.transactionDate
            BETWEEN :startDate AND :endDate
        """)
    BigDecimal sumByTypeAndPeriod(
            @Param("type")
            TransactionType type,

            @Param("startDate")
            LocalDate startDate,

            @Param("endDate")
            LocalDate endDate
    );

    long countByTransactionDateBetween(
            LocalDate startDate,
            LocalDate endDate
    );

    long countByCategoryId(Long categoryId);
}