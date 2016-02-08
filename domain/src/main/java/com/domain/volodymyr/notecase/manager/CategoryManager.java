package com.domain.volodymyr.notecase.manager;

import com.data.volodymyr.notecase.entity.Category;

import java.util.List;

/**
 * Created by vkret on 08.02.16.
 */
public interface CategoryManager {
    Category getCategoryById(int categoryId);

    void addCategory(Category category);

    void updateCategory(Category category);

    void deleteCategoryById(int categoryId);


    List<Category> getAllCategories();
}
