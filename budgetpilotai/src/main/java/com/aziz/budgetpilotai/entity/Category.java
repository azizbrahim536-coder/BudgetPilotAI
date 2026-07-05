package com.aziz.budgetpilotai.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"name", "type"}
                )
        }
)
public class Category {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
            nullable = false,
            length = 80
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private TransactionType type;

    @Column(length = 50)
    private String icon;

    @Column(
            nullable = false,
            length = 7
    )
    private String color;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Category() {
    }

    @PrePersist
    public void beforeInsert() {
        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (
                color == null ||
                        color.isBlank()
        ) {
            color = "#6366F1";
        }
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

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(
            TransactionType type
    ) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(
            String icon
    ) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(
            String color
    ) {
        this.color = color;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}