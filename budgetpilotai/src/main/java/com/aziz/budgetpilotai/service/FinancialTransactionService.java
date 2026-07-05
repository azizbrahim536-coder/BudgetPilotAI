package com.aziz.budgetpilotai.service;

import com.aziz.budgetpilotai.dto.TransactionRequest;
import com.aziz.budgetpilotai.entity.Category;
import com.aziz.budgetpilotai.entity.FinancialTransaction;
import com.aziz.budgetpilotai.entity.TransactionType;
import com.aziz.budgetpilotai.repository.FinancialTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialTransactionService {

    private final FinancialTransactionRepository
            transactionRepository;

    private final CategoryService categoryService;

    public FinancialTransactionService(
            FinancialTransactionRepository transactionRepository,
            CategoryService categoryService
    ) {
        this.transactionRepository =
                transactionRepository;

        this.categoryService =
                categoryService;
    }

    public List<FinancialTransaction>
    getTransactions(
            String search,
            TransactionType type,
            Long categoryId,
            LocalDate dateFrom,
            LocalDate dateTo
    ) {
        if (
                dateFrom != null &&
                        dateTo != null &&
                        dateFrom.isAfter(dateTo)
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Les dates sont invalides"
            );
        }

        return transactionRepository
                .searchTransactions(
                        cleanText(search),
                        type,
                        categoryId,
                        dateFrom,
                        dateTo
                );
    }

    public FinancialTransaction
    getTransactionById(
            Long id
    ) {
        return transactionRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transaction introuvable"
                                )
                );
    }

    public FinancialTransaction
    createTransaction(
            TransactionRequest request
    ) {
        Category category =
                categoryService
                        .getCategoryById(
                                request.categoryId()
                        );

        validateCategory(
                category,
                request.type()
        );

        FinancialTransaction transaction =
                new FinancialTransaction();

        applyRequest(
                transaction,
                request,
                category
        );

        return transactionRepository
                .save(transaction);
    }

    public FinancialTransaction
    updateTransaction(
            Long id,
            TransactionRequest request
    ) {
        FinancialTransaction transaction =
                getTransactionById(id);

        Category category =
                categoryService
                        .getCategoryById(
                                request.categoryId()
                        );

        validateCategory(
                category,
                request.type()
        );

        applyRequest(
                transaction,
                request,
                category
        );

        return transactionRepository
                .save(transaction);
    }

    public void deleteTransaction(
            Long id
    ) {
        transactionRepository.delete(
                getTransactionById(id)
        );
    }

    private void applyRequest(
            FinancialTransaction transaction,
            TransactionRequest request,
            Category category
    ) {
        transaction.setTitle(
                request.title().trim()
        );

        transaction.setDescription(
                cleanText(
                        request.description()
                )
        );

        transaction.setAmount(
                request.amount()
        );

        transaction.setType(
                request.type()
        );

        transaction.setCategory(
                category
        );

        transaction.setTransactionDate(
                request.transactionDate()
        );
    }

    private void validateCategory(
            Category category,
            TransactionType type
    ) {
        if (
                category.getType() != type
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Le type de la catégorie est incorrect"
            );
        }
    }

    private String cleanText(
            String value
    ) {
        if (
                value == null ||
                        value.isBlank()
        ) {
            return null;
        }

        return value.trim();
    }
}