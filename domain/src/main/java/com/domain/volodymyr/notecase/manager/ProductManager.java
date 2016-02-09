package com.domain.volodymyr.notecase.manager;

import android.database.Cursor;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by volodymyr on 07.02.16.
 */
public interface ProductManager {
    Product getProductById(int id);

    List<Product> getAllProducts(Timestamp since, Timestamp till);

    boolean updateProduct(Product product);

    boolean addProduct(Product product);

    boolean deleteProductById(int id);

    List<Product> getProductsByCategoryId(int categoryId);

    Cursor getProductNameCursor();

    Cursor suggestProductName(String partialProductName);

    Map<Category, Double> getExpensesGroupedByCategories(Timestamp since, Timestamp till);

    boolean syncProducts();

}
