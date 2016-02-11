package com.domain.volodymyr.notecase.manager;

import android.content.Context;
import android.database.Cursor;

import com.data.volodymyr.notecase.dao.ProductSQLiteDAO;
import com.data.volodymyr.notecase.dao.ProductSQLiteDAOImpl;
import com.data.volodymyr.notecase.daonetwork.ProductNetworkDAO;
import com.data.volodymyr.notecase.daonetwork.ProductNetworkDAOImpl;
import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by volodymyr on 07.02.16.
 */
public class ProductManagerImpl implements ProductManager {

    private ProductNetworkDAO productNetworkDAO = new ProductNetworkDAOImpl();
    private ProductSQLiteDAO productSQLiteDAO;

    public ProductManagerImpl(Context context) {
        this.productSQLiteDAO = new ProductSQLiteDAOImpl(context);
    }

    @Override
    public boolean updateProduct(Product product) {
        boolean success = productNetworkDAO.updateProduct(product);
        if (!success) {
            product.setDirty(true);
        }
        productSQLiteDAO.updateProduct(product);

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
        int id = productSQLiteDAO.addProduct(product);
        product.setId(id);

        boolean uploaded = productNetworkDAO.addProduct(product);
        if (uploaded) {
            product.setDirty(false);
            productSQLiteDAO.updateProduct(product);
        }
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

        boolean renderAgain = false;
        //upload all newly updated products from server
        List<Product> updatedProducts = productNetworkDAO.getProductsSinceUpdateTimestamp(productSQLiteDAO.getLastSyncTimestamp());
        if (updatedProducts!=null){
            for (Product product : updatedProducts) {
                Product deviceProd = productSQLiteDAO.getProductById(product.getId());
                if (deviceProd == null) {
                    product.setDirty(false);
                    productSQLiteDAO.addProduct(product);
                } else {
                    productSQLiteDAO.updateProduct(product);
                }
                renderAgain = true;
            }
            productSQLiteDAO.updateLastSyncTimestamp(new Timestamp(System.currentTimeMillis()));
        }
        return renderAgain;
    }

    @Override
    public List<Product> getProductsByCategoryId(int categoryId) {
        return productSQLiteDAO.getProductsByCategoryId(categoryId);
    }

    @Override
    public Cursor getProductNameCursor() {
        return productSQLiteDAO.getProductNameCursor();
    }

    @Override
    public Cursor suggestProductName(String partialProductName) {
        return productSQLiteDAO.suggestProductName(partialProductName);
    }

    @Override
    public Map<Category, Double> getExpensesGroupedByCategories(Timestamp since, Timestamp till) {
        return productSQLiteDAO.getExpensesGroupedByCategories(since, till);
    }

}
