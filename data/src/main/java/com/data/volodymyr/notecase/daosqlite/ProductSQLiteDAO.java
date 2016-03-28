package com.data.volodymyr.notecase.daosqlite;

import android.database.Cursor;

import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by volodymyr on 31.01.16.
 */
public interface ProductSQLiteDAO {

    void addProduct(Product product);

    void updateProduct(Product product);

    /**
     *
     * @param uuid
     * @return product by id or null if product doesnt exist
     */
    Product getProductByUuid(String uuid);

    List<Product> getAllProducts(Timestamp since, Timestamp till);

    List<Product> getDirtyProducts();

    List<Product> getProductsByCategoryId(int categoryId);

    Map<Category, Double> getProductsGroupedByCategories(Timestamp since, Timestamp till);

    Cursor getProductNameCursor();

    Cursor suggestProductName(String partialProductName);

    Timestamp getLastSyncTimestamp();

    void updateLastSyncTimestamp(Timestamp timestamp);

}
