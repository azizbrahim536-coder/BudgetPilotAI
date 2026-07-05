package com.aziz.budgetpilotai.dto;

import com.aziz.budgetpilotai.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(
                message = "Le nom est obligatoire"
        )
        @Size(
                max = 80,
                message = "Le nom est trop long"
        )
        String name,

        @NotNull(
                message = "Le type est obligatoire"
        )
        TransactionType type,

        @Size(max = 50)
        String icon,

        @Pattern(
                regexp = "^#[0-9A-Fa-f]{6}$",
                message = "Couleur invalide"
        )
        String color

) {
}