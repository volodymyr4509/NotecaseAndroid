package com.domain.volodymyr.notecase.manager;

import com.data.volodymyr.notecase.entity.Product;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by volodymyr on 07.02.16.
 */
public interface ProductManager {
    Product getProductById(int id);
    List<Product> getAllProducts(Timestamp since, Timestamp till);
    boolean updateProduct(Product product);
    boolean addProduct(Product product);
    boolean deleteProductById(int id);
}
