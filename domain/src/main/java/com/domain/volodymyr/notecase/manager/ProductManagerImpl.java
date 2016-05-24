package com.domain.volodymyr.notecase.manager;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.util.Log;

import com.data.volodymyr.notecase.daonetwork.ProductNetworkDAO;
import com.data.volodymyr.notecase.daonetwork.ProductNetworkDAOImpl;
import com.data.volodymyr.notecase.daosqlite.ProductSQLiteDAO;
import com.data.volodymyr.notecase.daosqlite.ProductSQLiteDAOImpl;
import com.data.volodymyr.notecase.entity.Category;
import com.data.volodymyr.notecase.entity.Product;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by volodymyr on 07.02.16.
 */
public class ProductManagerImpl implements ProductManager {

    //TODO dissable products/categories/users not delete them
    private ProductNetworkDAO productNetworkDAO;
    private ProductSQLiteDAO productSQLiteDAO;

    public ProductManagerImpl(Context context) {
        this.productSQLiteDAO = new ProductSQLiteDAOImpl(context);
        this.productNetworkDAO = new ProductNetworkDAOImpl(context);
    }

    @Override
    public boolean updateProduct(Product product) {
        boolean success = productNetworkDAO.updateProduct(product);
        product.setDirty(!success);

        productSQLiteDAO.updateProduct(product);

        return success;
    }

    @Override
    public Product getProductByUuid(String uuid) {
        return productSQLiteDAO.getProductByUuid(uuid);
    }

    @Override
    public List<Product> getAllProducts(Timestamp since, Timestamp till) {
        return productSQLiteDAO.getAllProducts(since, till);
    }

    @Override
    public boolean addProduct(Product product) {
        productSQLiteDAO.addProduct(product);

        boolean uploaded = productNetworkDAO.addProduct(product);
        if (uploaded) {
            product.setDirty(false);
            productSQLiteDAO.updateProduct(product);
        }
        return uploaded;
    }

    @Override
    public boolean deleteProductByUuid(String uuid) {

        Product product = productSQLiteDAO.getProductByUuid(uuid);
        if (product == null){
            return true;
        }
        boolean deleted = productNetworkDAO.deleteProductByUuid(uuid);

        product.setDirty(!deleted);
        product.setEnabled(false);
        productSQLiteDAO.updateProduct(product);

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
                Product deviceProd = productSQLiteDAO.getProductByUuid(product.getUuid());
                product.setDirty(false);
                if (deviceProd == null) {
                    productSQLiteDAO.addProduct(product);
                } else {
                    productSQLiteDAO.updateProduct(product);
                }
                renderAgain = true;
            }
            productSQLiteDAO.updateLastSyncTimestamp(new Timestamp(System.currentTimeMillis()-5000));
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
        return productSQLiteDAO.getProductsGroupedByCategories(since, till);
    }

}
