package com.data.volodymyr.notecase.daonetwork;

import com.data.volodymyr.notecase.entity.Product;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by volodymyr on 07.02.16.
 */
public interface ProductNetworkDAO {

    Product getProduct(String uuid);

    boolean updateProduct(Product product);

    boolean addProduct(Product product);

    boolean deleteProductByUuid(String uuid);

    List<Product> getProductsSinceUpdateTimestamp(Timestamp lastUpdateTimestamp);

}
