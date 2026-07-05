package com.aziz.budgetpilotai.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "budgets",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "category_id",
                                "budget_year",
                                "budget_month"
                        }
                )
        }
)
public class Budget {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    @Column(
            name = "limit_amount",
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal limitAmount;

    @Column(
            name = "budget_year",
            nullable = false
    )
    private Integer budgetYear;

    @Column(
            name = "budget_month",
            nullable = false
    )
    private Integer budgetMonth;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Budget() {
    }

    @PrePersist
    public void beforeInsert() {
        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void beforeUpdate() {
        updatedAt =
                LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(
            Category category
    ) {
        this.category = category;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(
            BigDecimal limitAmount
    ) {
        this.limitAmount = limitAmount;
    }

    public Integer getBudgetYear() {
        return budgetYear;
    }

    public void setBudgetYear(
            Integer budgetYear
    ) {
        this.budgetYear = budgetYear;
    }

    public Integer getBudgetMonth() {
        return budgetMonth;
    }

    public void setBudgetMonth(
            Integer budgetMonth
    ) {
        this.budgetMonth = budgetMonth;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}