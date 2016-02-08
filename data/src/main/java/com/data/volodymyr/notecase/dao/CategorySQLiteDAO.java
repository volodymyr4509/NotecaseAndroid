package com.data.volodymyr.notecase.dao;

import com.data.volodymyr.notecase.entity.Category;

import java.util.List;

/**
 * Created by volodymyr on 31.01.16.
 */
public interface CategorySQLiteDAO {
    void addCategory(Category category);

    void updateCategory(Category category);

    void deleteCategoryById(int categoryId);

    Category getCategoryById(int categoryId);

    List<Category> getAllCategories();


}
