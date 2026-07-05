package com.aziz.budgetpilotai.repository;

import com.aziz.budgetpilotai.entity.Category;
import com.aziz.budgetpilotai.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    List<Category>
    findAllByOrderByTypeAscNameAsc();

    List<Category>
    findByTypeOrderByNameAsc(
            TransactionType type
    );

    boolean existsByNameIgnoreCaseAndType(
            String name,
            TransactionType type
    );
}