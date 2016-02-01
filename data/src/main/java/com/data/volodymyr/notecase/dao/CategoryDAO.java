package com.data.volodymyr.notecase.dao;

import com.data.volodymyr.notecase.entity.Category;

import java.util.List;

/**
 * Created by volodymyr on 31.01.16.
 */
public interface CategoryDAO {
    public void addCategory(Category category);
    public void updateCategory(Category category) ;
    public void deleteCategoryById(int categoryId) ;
    public Category getCategoryById(int categoryId) ;
    public List<Category> getAllCategories();



    }
