package com.domain.volodymyr.notecase.manager;

import android.content.Context;

import com.data.volodymyr.notecase.dao.CategorySQLiteDAO;
import com.data.volodymyr.notecase.dao.CategorySQLiteDAOImpl;
import com.data.volodymyr.notecase.entity.Category;

import java.util.List;

/**
 * Created by vkret on 08.02.16.
 */
public class CategoryManagerImpl implements CategoryManager{
    private Context context;

//    private ProductNetworkDAO productNetworkDAO = new ProductNetworkDAOImpl();
    private CategorySQLiteDAO categorySQLiteDAO = new CategorySQLiteDAOImpl(context);

    public CategoryManagerImpl(Context context) {
        this.context = context;
    }

    @Override
    public Category getCategoryById(int categoryId) {
        return categorySQLiteDAO.getCategoryById(categoryId);
    }

    @Override
    public void addCategory(Category category) {
        categorySQLiteDAO.addCategory(category);
    }

    @Override
    public void updateCategory(Category category) {
        categorySQLiteDAO.updateCategory(category);
    }

    @Override
    public void deleteCategoryById(int categoryId) {
        categorySQLiteDAO.deleteCategoryById(categoryId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categorySQLiteDAO.getAllCategories();
    }
}
