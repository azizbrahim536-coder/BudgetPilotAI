package com.aziz.budgetpilotai.dto;

import com.aziz.budgetpilotai.entity.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(

        @NotBlank(
                message = "Le titre est obligatoire"
        )
        @Size(max = 150)
        String title,

        @Size(max = 3000)
        String description,

        @NotNull(
                message = "Le montant est obligatoire"
        )
        @DecimalMin(
                value = "0.01",
                message = "Le montant doit être supérieur à zéro"
        )
        BigDecimal amount,

        @NotNull(
                message = "Le type est obligatoire"
        )
        TransactionType type,

        @NotNull(
                message = "La catégorie est obligatoire"
        )
        Long categoryId,

        @NotNull(
                message = "La date est obligatoire"
        )
        LocalDate transactionDate

) {
}