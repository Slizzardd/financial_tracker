package ua.com.alevel.facades.impl;

import org.springframework.stereotype.Service;
import ua.com.alevel.facades.CategoryFacade;
import ua.com.alevel.services.CategoryService;

@Service
public class CategoryFacadeImpl implements CategoryFacade{

    private final CategoryService categoryService;

    public CategoryFacadeImpl(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
}
