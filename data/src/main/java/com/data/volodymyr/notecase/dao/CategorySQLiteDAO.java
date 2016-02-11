package com.data.volodymyr.notecase.dao;

import com.data.volodymyr.notecase.entity.Category;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by volodymyr on 31.01.16.
 */
public interface CategorySQLiteDAO {
    int addCategory(Category category);

    void updateCategory(Category category);

    void deleteCategoryById(int categoryId);

    Category getCategoryById(int categoryId);

    List<Category> getAllCategories();

    List<Category> getDirtyCategories();

    Timestamp getLastSyncTimestamp();

    void updateLastSyncTimestamp(Timestamp timestamp);

}
