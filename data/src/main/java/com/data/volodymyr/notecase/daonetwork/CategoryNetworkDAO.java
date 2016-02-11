package com.data.volodymyr.notecase.daonetwork;

import com.data.volodymyr.notecase.entity.Category;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by volodymyr on 10.02.16.
 */
public interface CategoryNetworkDAO {

    Category getCategory(int id);

    boolean updateCategory(Category category);

    boolean addCategory(Category category);

    boolean deleteCategory(int id);

    List<Category> getCategoriesSinceUpdateTimestamp(Timestamp lastUpdateTimestamp);

}
