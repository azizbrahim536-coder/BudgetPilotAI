package com.aziz.budgetpilotai.controller;

import com.aziz.budgetpilotai.dto.TransactionRequest;
import com.aziz.budgetpilotai.entity.FinancialTransaction;
import com.aziz.budgetpilotai.entity.TransactionType;
import com.aziz.budgetpilotai.service.FinancialTransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class FinancialTransactionController {

    private final FinancialTransactionService
            transactionService;

    public FinancialTransactionController(
            FinancialTransactionService transactionService
    ) {
        this.transactionService =
                transactionService;
    }

    @GetMapping
    public List<FinancialTransaction>
    getTransactions(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            TransactionType type,

            @RequestParam(required = false)
            Long categoryId,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE
            )
            LocalDate dateTo
    ) {
        return transactionService
                .getTransactions(
                        search,
                        type,
                        categoryId,
                        dateFrom,
                        dateTo
                );
    }

    @GetMapping("/{id}")
    public FinancialTransaction
    getTransactionById(
            @PathVariable Long id
    ) {
        return transactionService
                .getTransactionById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialTransaction
    createTransaction(
            @Valid
            @RequestBody
            TransactionRequest request
    ) {
        return transactionService
                .createTransaction(request);
    }

    @PutMapping("/{id}")
    public FinancialTransaction
    updateTransaction(
            @PathVariable Long id,

            @Valid
            @RequestBody
            TransactionRequest request
    ) {
        return transactionService
                .updateTransaction(
                        id,
                        request
                );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(
            @PathVariable Long id
    ) {
        transactionService
                .deleteTransaction(id);
    }
}