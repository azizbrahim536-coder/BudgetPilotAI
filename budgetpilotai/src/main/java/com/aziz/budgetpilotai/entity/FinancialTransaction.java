package com.aziz.budgetpilotai.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "financial_transactions"
)
public class FinancialTransaction {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            nullable = false,
            length = 150
    )
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(
            nullable = false,
            precision = 14,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private TransactionType type;

    @ManyToOne
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public FinancialTransaction() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(
            String title
    ) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(
            BigDecimal amount
    ) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(
            TransactionType type
    ) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(
            Category category
    ) {
        this.category = category;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(
            LocalDate transactionDate
    ) {
        this.transactionDate =
                transactionDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}