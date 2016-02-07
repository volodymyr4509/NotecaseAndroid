package com.data.volodymyr.notecase.daonetwork;

import com.data.volodymyr.notecase.entity.Product;

/**
 * Created by volodymyr on 07.02.16.
 */
public interface ProductPiDAO {
    Product getProduct(int id);

    boolean updateProduct(Product product);
}
