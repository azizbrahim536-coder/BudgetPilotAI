package com.aziz.budgetpilotai.controller;

import com.aziz.budgetpilotai.dto.CategoryRequest;
import com.aziz.budgetpilotai.entity.Category;
import com.aziz.budgetpilotai.entity.TransactionType;
import com.aziz.budgetpilotai.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService =
                categoryService;
    }

    @GetMapping
    public List<Category> getCategories(
            @RequestParam(required = false)
            TransactionType type
    ) {
        return categoryService
                .getCategories(type);
    }

    @GetMapping("/{id}")
    public Category getCategoryById(
            @PathVariable Long id
    ) {
        return categoryService
                .getCategoryById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(
            @Valid
            @RequestBody
            CategoryRequest request
    ) {
        return categoryService
                .createCategory(request);
    }

    @PutMapping("/{id}")
    public Category updateCategory(
            @PathVariable Long id,

            @Valid
            @RequestBody
            CategoryRequest request
    ) {
        return categoryService
                .updateCategory(
                        id,
                        request
                );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable Long id
    ) {
        categoryService
                .deleteCategory(id);
    }
}