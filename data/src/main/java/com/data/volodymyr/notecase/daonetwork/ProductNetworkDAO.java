package com.data.volodymyr.notecase.daonetwork;

import com.data.volodymyr.notecase.entity.Product;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by volodymyr on 07.02.16.
 */
public interface ProductNetworkDAO {

    Product getProduct(int id);

    boolean updateProduct(Product product);

    boolean addProduct(Product product);

    boolean deleteProduct(int id);

    List<Product> getProductsSinceUpdateTimestamp(Timestamp lastUpdateTimestamp);

}
