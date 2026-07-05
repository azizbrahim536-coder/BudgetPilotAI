package com.aziz.budgetpilotai.service;

import com.aziz.budgetpilotai.dto.CategoryRequest;
import com.aziz.budgetpilotai.entity.Category;
import com.aziz.budgetpilotai.entity.TransactionType;
import com.aziz.budgetpilotai.repository.BudgetRepository;
import com.aziz.budgetpilotai.repository.CategoryRepository;
import com.aziz.budgetpilotai.repository.FinancialTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final FinancialTransactionRepository
            transactionRepository;

    private final BudgetRepository budgetRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            FinancialTransactionRepository transactionRepository,
            BudgetRepository budgetRepository
    ) {
        this.categoryRepository =
                categoryRepository;

        this.transactionRepository =
                transactionRepository;

        this.budgetRepository =
                budgetRepository;
    }

    public List<Category> getCategories(
            TransactionType type
    ) {
        if (type == null) {
            return categoryRepository
                    .findAllByOrderByTypeAscNameAsc();
        }

        return categoryRepository
                .findByTypeOrderByNameAsc(type);
    }

    public Category getCategoryById(
            Long id
    ) {
        return categoryRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Catégorie introuvable"
                                )
                );
    }

    public Category createCategory(
            CategoryRequest request
    ) {
        String name =
                request.name().trim();

        if (
                categoryRepository
                        .existsByNameIgnoreCaseAndType(
                                name,
                                request.type()
                        )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cette catégorie existe déjà"
            );
        }

        Category category =
                new Category();

        category.setName(name);
        category.setType(request.type());
        category.setIcon(request.icon());

        category.setColor(
                request.color() == null
                        ? "#6366F1"
                        : request.color()
        );

        return categoryRepository
                .save(category);
    }

    public Category updateCategory(
            Long id,
            CategoryRequest request
    ) {
        Category category =
                getCategoryById(id);

        category.setName(
                request.name().trim()
        );

        category.setType(
                request.type()
        );

        category.setIcon(
                request.icon()
        );

        category.setColor(
                request.color() == null
                        ? "#6366F1"
                        : request.color()
        );

        return categoryRepository
                .save(category);
    }

    public void deleteCategory(
            Long id
    ) {
        Category category =
                getCategoryById(id);

        if (
                transactionRepository
                        .countByCategoryId(id) > 0
                        ||
                        budgetRepository
                                .countByCategoryId(id) > 0
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La catégorie est déjà utilisée"
            );
        }

        categoryRepository
                .delete(category);
    }
}