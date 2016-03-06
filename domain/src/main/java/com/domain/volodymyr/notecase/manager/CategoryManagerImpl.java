package com.domain.volodymyr.notecase.manager;

import android.content.Context;

import com.data.volodymyr.notecase.daosqlite.CategorySQLiteDAO;
import com.data.volodymyr.notecase.daosqlite.CategorySQLiteDAOImpl;
import com.data.volodymyr.notecase.daonetwork.CategoryNetworkDAO;
import com.data.volodymyr.notecase.daonetwork.CategoryNetworkDAOImpl;
import com.data.volodymyr.notecase.daosqlite.UserSQLiteDAO;
import com.data.volodymyr.notecase.daosqlite.UserSQLiteDAOImpl;
import com.data.volodymyr.notecase.entity.Category;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by vkret on 08.02.16.
 */
public class CategoryManagerImpl implements CategoryManager {

    private CategoryNetworkDAO categoryNetworkDAO;
    private CategorySQLiteDAO categorySQLiteDAO;

    public CategoryManagerImpl(Context context) {
        this.categorySQLiteDAO = new CategorySQLiteDAOImpl(context);
        this.categoryNetworkDAO = new CategoryNetworkDAOImpl(context);
    }

    @Override
    public Category getCategoryById(int categoryId) {
        return categorySQLiteDAO.getCategoryById(categoryId);
    }

    @Override
    public boolean addCategory(Category category) {
        int id = categorySQLiteDAO.addCategory(category);
        category.setId(id);
        boolean uploaded = categoryNetworkDAO.addCategory(category);
        if (uploaded) {
            category.setDirty(false);
            categorySQLiteDAO.updateCategory(category);
        }
        return uploaded;
    }

    @Override
    public boolean updateCategory(Category category) {
        boolean success = categoryNetworkDAO.updateCategory(category);
        if (!success) {
            category.setDirty(true);
        }
        categorySQLiteDAO.updateCategory(category);
        return success;
    }

    @Override
    public boolean deleteCategoryById(int id) {
        boolean deleted = categoryNetworkDAO.deleteCategory(id);
        if (deleted) {
            categorySQLiteDAO.deleteCategoryById(id);
        }
        return deleted;
    }

    @Override
    public List<Category> getAllCategories() {
        return categorySQLiteDAO.getAllCategories();
    }

    /**
     * @return true if there were unsynchronized products. false otherwise
     */
    @Override
    public boolean syncCategories() {
        //upload dirty categories from device and change dirty = false;
        List<Category> dirtyCategories = categorySQLiteDAO.getDirtyCategories();
        for (Category category : dirtyCategories) {
            boolean uploaded = categoryNetworkDAO.addCategory(category);
            if (uploaded) {
                category.setDirty(false);
                categorySQLiteDAO.updateCategory(category);
            }
        }

        boolean renderAgain = false;
        //upload all newly updated categories from server
        List<Category> updatedCategories = categoryNetworkDAO.getCategoriesSinceUpdateTimestamp(categorySQLiteDAO.getLastSyncTimestamp());
        if (updatedCategories != null) {
            for (Category category : updatedCategories) {
                Category deviceCategory = categorySQLiteDAO.getCategoryById(category.getId());
                category.setDirty(false);
                if (deviceCategory == null) {
                    categorySQLiteDAO.addCategory(category);
                } else {
                    categorySQLiteDAO.updateCategory(category);
                }
                renderAgain = true;
            }
            categorySQLiteDAO.updateLastSyncTimestamp(new Timestamp(System.currentTimeMillis()));
        }
        return renderAgain;
    }

}
