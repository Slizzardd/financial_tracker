package ua.com.alevel.services.impl;

import org.springframework.stereotype.Service;
import ua.com.alevel.persistence.repositories.CategoryRepository;
import ua.com.alevel.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
}
