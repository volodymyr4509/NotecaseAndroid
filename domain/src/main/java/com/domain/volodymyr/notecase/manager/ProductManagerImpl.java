package com.domain.volodymyr.notecase.manager;

import android.content.Context;

import com.data.volodymyr.notecase.dao.ProductSQLiteDAO;
import com.data.volodymyr.notecase.dao.ProductSQLiteDAOImpl;
import com.data.volodymyr.notecase.daonetwork.ProductNetworkDAO;
import com.data.volodymyr.notecase.daonetwork.ProductNetworkDAOImpl;
import com.data.volodymyr.notecase.entity.Product;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by volodymyr on 07.02.16.
 */
public class ProductManagerImpl implements ProductManager {
    private Context context;

    private ProductNetworkDAO productNetworkDAO = new ProductNetworkDAOImpl();
    private ProductSQLiteDAO productSQLiteDAO = new ProductSQLiteDAOImpl(context);

    public ProductManagerImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean updateProduct(Product product) {
        boolean success = productNetworkDAO.updateProduct(product);
        if (success) {
            product.setDirty(false);
            productSQLiteDAO.updateProduct(product);
        }
        return success;
    }

    @Override
    public Product getProductById(int id) {
        return productSQLiteDAO.getProductById(id);
    }

    @Override
    public List<Product> getAllProducts(Timestamp since, Timestamp till) {
        List<Product> productList = productSQLiteDAO.getAllProducts(since, till);
        return productList;
    }

    @Override
    public boolean addProduct(Product product) {
        boolean uploaded = productNetworkDAO.addProduct(product);
        if (uploaded) {
            product.setDirty(false);
        }
        productSQLiteDAO.addProduct(product);
        return uploaded;
    }

    @Override
    public boolean deleteProductById(int id) {
        boolean deleted = productNetworkDAO.deleteProduct(id);
        if (deleted) {
            productSQLiteDAO.deleteProductById(id);
        }
        return deleted;
    }

    /**
     * @return true if there were unsynchronized products. false otherwise
     */
    public boolean syncProducts() {
        //upload dirty products from device and change dirty = false;
        List<Product> dirtyProducts = productSQLiteDAO.getDirtyProducts();
        for (Product product : dirtyProducts) {
            boolean uploaded = productNetworkDAO.addProduct(product);
            if (uploaded) {
                product.setDirty(false);
                productSQLiteDAO.updateProduct(product);
            }
        }

        boolean shouldRenderAgain = false;
        //upload all newly updated products from server
        List<Product> updatedProducts = productNetworkDAO.getProductsSinceUpdateTimestamp(productSQLiteDAO.getLastSyncTimestamp());
        for (Product product : updatedProducts) {
            Product deviceProd = productSQLiteDAO.getProductById(product.getId());
            if (deviceProd == null) {
                productSQLiteDAO.addProduct(product);
            } else {
                productSQLiteDAO.updateProduct(product);
            }
            productSQLiteDAO.updateProduct(product);
            shouldRenderAgain = true;
        }

        productSQLiteDAO.updateLastSyncTimestamp(new Timestamp(System.currentTimeMillis()));
        return shouldRenderAgain;
    }

}
