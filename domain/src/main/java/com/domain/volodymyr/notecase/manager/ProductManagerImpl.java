package com.domain.volodymyr.notecase.manager;

import com.data.volodymyr.notecase.daonetwork.ProductPiDAO;
import com.data.volodymyr.notecase.daonetwork.ProductPiDAOImpl;
import com.data.volodymyr.notecase.entity.Product;

/**
 * Created by volodymyr on 07.02.16.
 */
public class ProductManagerImpl implements ProductManager {

    ProductPiDAO productPiDAO = new ProductPiDAOImpl();

    @Override
    public boolean updateProduct(Product product) {
        return productPiDAO.updateProduct(product);
    }
}
